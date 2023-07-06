package com.hihonor.datacollector.realtime.transform;

import com.bytedance.bitsail.base.execution.ExecutionEnviron;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.option.CommonOptions;
import com.bytedance.bitsail.connector.legacy.kafka.constants.KafkaConstants;
import com.bytedance.bitsail.connector.legacy.messagequeue.source.option.BaseMessageQueueReaderOptions;
import com.bytedance.bitsail.flink.core.transformer.FlinkDataTransformerDAGBuilder;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hihonor.datacollector.realtime.crypto.utils.HexUtils;
import com.hihonor.datacollector.realtime.crypto.utils.InflaterUtil;
import com.hihonor.datacollector.realtime.entity.*;
import com.hihonor.datacollector.realtime.udf.AESDecryptHAUDF;
import com.hihonor.datacollector.realtime.utils.GsonUtil;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bytedance.bitsail.flink.core.serialization.AbstractDeserializationSchema.*;
import static com.hihonor.datacollector.realtime.crypto.encryptor.AESCryptorBase64.UTF_8;
import static com.hihonor.datacollector.realtime.crypto.utils.Common.DEVICE_ID;
import static com.hihonor.datacollector.realtime.crypto.utils.Common.DEVICE_ID_TYPE_CD;
import static com.hihonor.datacollector.realtime.utils.GsonUtil.getGson;

public class DecryptTransformDAGBuilder extends FlinkDataTransformerDAGBuilder<Row, Row> {

    private static final Logger LOG = LoggerFactory.getLogger(DecryptTransformDAGBuilder.class);

    private HashMap<String, String> tableAndPackageNameMap = new HashMap<>();

    private OutPutTables outPutTables;

    private String[] jsonLocationList;

    private List<Object> realtimeAppIds;

    private boolean isDecrypt;
    private boolean isUnpack;
    private boolean transformerAppidCheck;

    @Override
    public void configure(ExecutionEnviron execution,
                          BitSailConfiguration transformConfiguration) throws Exception {
        super.configure(execution, transformConfiguration);
        this.jobConf.merge(transformConfiguration, true, new Function[0]);
        isDecrypt = jobConf.get(BaseMessageQueueReaderOptions.IS_DECRYPT);
        isUnpack = jobConf.get(BaseMessageQueueReaderOptions.IS_UNPACK);
        transformerAppidCheck = jobConf.get(BaseMessageQueueReaderOptions.TRANSFORMER_APPID_CHECK);
        List<Object> tableNames = ConfigFactory.load(
                "conf/repair_tables_name.conf").getList("tablenames").unwrapped();
        List<Object> packageNames = ConfigFactory.load(
                "conf/repair_packages_name.conf").getList("packagenames").unwrapped();

        realtimeAppIds = ConfigFactory.load(
                "conf/realtime_packages_name.conf").getList("packagenames").unwrapped();

        if (null != tableNames) {
            for (int i = 0; i < tableNames.size(); i++) {
                String[] subPackages = ((String) packageNames.get(i)).split(" ", -1);
                String tableName = (String) tableNames.get(i);
                Arrays.stream(subPackages).forEach(s -> tableAndPackageNameMap.put(s, tableName));
            }
        }

        Map<String, Object> objectMap =
                ConfigFactory.load("conf/repair_event.json").getObject("tableInfos").unwrapped();

        outPutTables = GsonUtil.fromJson(getGson().toJson(objectMap),
                new TypeToken<OutPutTables>() {
                }.getType());

        jsonLocationList = outPutTables.getTablesInformation()[0].getJsonLocationList().split(",");
    }

    @Override
    public DataStream<Row> addDiffTransform(DataStream<Row> dataStream, int i) throws Exception {
        return null;
    }

    @Override
    public DataStream<Row> addSameTransform(DataStream<Row> dataStream, int readerParallelism) throws Exception {
        return dataStream.flatMap(new FlatMapFunction<Row, Row>() {
            @Override
            public void flatMap(Row row, Collector<Row> collector) throws Exception {
                // json 反序列化
                String key = new String(Optional.ofNullable((byte[]) row.getField(DUMP_ROW_KEY_INDEX)).orElse("".getBytes()),
                        StandardCharsets.UTF_8);
                String input = new String((byte[]) row.getField(DUMP_ROW_VALUE_INDEX), StandardCharsets.UTF_8);
                String partition = (String) row.getField(DUMP_ROW_PARTITION_INDEX);
                Long currentOffset = (Long) row.getField(DUMP_ROW_OFFSET_INDEX);
                if (!StringUtils.isNullOrWhitespaceOnly(input)) {
                    String[] rows = input.split("\u0001");
                    if (rows.length == 3 && !StringUtils.isNullOrWhitespaceOnly(rows[0])) {
                        try {
                            JsonObject jsonObject = GsonUtil.fromJson(rows[0], JsonObject.class);
                            JsonObject headerJsonObject = jsonObject.getAsJsonObject("header");
                            if (null != headerJsonObject) {
                                String appId = headerJsonObject.get("appid").getAsString();
                                if (transformerAppidCheck) {
                                    if (realtimeAppIds.contains(appId)) {
                                        // 开启实时入湖白名单
                                        output(collector, key, input, partition, currentOffset, rows, jsonObject, headerJsonObject, appId);
                                    }
                                } else {
                                    output(collector, key, input, partition, currentOffset, rows, jsonObject, headerJsonObject, appId);
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            LOG.error("gson parse error : \n " + rows[0]);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            LOG.error("gson parse error : \n " + rows[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.error("decrypt data error cause by " + e.getCause());
                        }
                    }
                }
            }
        }).uid(geTransformerName()).name(geTransformerName()).setParallelism(readerParallelism);

    }

    private void output(Collector<Row> collector, String key, String input, String partition, Long currentOffset,
                        String[] rows,
                        JsonObject jsonObject,
                        JsonObject headerJsonObject,
                        String appId) throws IOException {
        String tableName = tableAndPackageNameMap.getOrDefault(appId, "unknown");
        if (!isDecrypt) {
            // 需要解密解压数据拆包
            decrypt(collector, key, partition, currentOffset, rows, jsonObject, headerJsonObject, appId, tableName);
        } else if (isUnpack) {
            decompressAndUnpack(collector, key, partition, currentOffset, rows, jsonObject, headerJsonObject, appId, tableName);
        } else {
            //  原始加密压缩数据
            Row output = Row.of(key, input.getBytes(StandardCharsets.UTF_8), partition, currentOffset, tableName);
            collector.collect(output);
        }
    }

    private void decompressAndUnpack(Collector<Row> collector, String key, String partition, Long currentOffset,
                                     String[] rows, JsonObject jsonObject,
                                     JsonObject headerJsonObject, String appId, String tableName) throws IOException {
        String eventEn = jsonObject.get("event").getAsString();
        byte[] bytes = HexUtils.toBytes(eventEn);
        String event = new String(InflaterUtil.decompress(bytes), UTF_8);
        // event 特殊字符剔除
        String eventDe = event.replaceAll("\\*|\t|\r|\n", "");

        EventsDecrypt eventsDecrypt = GsonUtil.fromJson(eventDe,
                new com.google.common.reflect.TypeToken<EventsDecrypt>() {
                }.getType());

        collectUnpackEvent(collector, key, partition, currentOffset, rows, headerJsonObject, appId, tableName, rows[1], eventsDecrypt);
    }

    private void decrypt(Collector<Row> collector, String key, String partition, Long currentOffset, String[] rows,
                         JsonObject jsonObject, JsonObject headerJsonObject, String appId, String tableName) {
        String cryptoIp = rows[1];
        if (!StringUtils.isNullOrWhitespaceOnly(cryptoIp) && cryptoIp.length() >= 64) {
            cryptoIp = AESDecryptHAUDF.decryptIPBySDK(cryptoIp);
        }

        EventsDecrypt eventsDecrypt = AESDecryptHAUDF.decryptEvents(jsonObject);
        collectUnpackEvent(collector, key, partition, currentOffset, rows, headerJsonObject, appId, tableName, cryptoIp, eventsDecrypt);
    }

    private void collectUnpackEvent(Collector<Row> collector, String key, String partition, Long currentOffset, String[] rows, JsonObject headerJsonObject, String appId, String tableName, String cryptoIp, EventsDecrypt eventsDecrypt) {
        Header header = GsonUtil.fromJson(headerJsonObject.toString(), Header.class);
        if (null != eventsDecrypt) {
            Events[] events = eventsDecrypt.getEvents();
            if (null != events) {
                String finalCryptoIp = cryptoIp;
                Arrays.stream(events).forEach(event -> {
                            SDKEntity sdkEntity = new SDKEntity(header, eventsDecrypt.getEventsCommon(), event, finalCryptoIp, rows[2]);
                            // 输出dwb层的数据形式
                            HashMap<String, Object> fieldMap = Maps.newHashMap();
                            EventsDecrypt.getGenericFieldValueMap(sdkEntity, fieldMap, null);
                            Tuple2<String, String> deviceType = getFinalDeviceIdTypes(sdkEntity.getEventsCommon());
                            fieldMap.put(DEVICE_ID, deviceType.f0);
                            fieldMap.put(DEVICE_ID_TYPE_CD, deviceType.f1);
                            String dumpValues = Arrays.stream(jsonLocationList).map(filed -> String.valueOf(
                                    fieldMap.get(filed)).replaceAll("\t", "")).collect(Collectors.joining("\t"));
                            Row output = Row.of(key, dumpValues.getBytes(StandardCharsets.UTF_8), partition, currentOffset, tableName, appId, event.getEvent());
                            collector.collect(output);
                        }
                );
            }
        }
    }

    @Override
    public String geTransformerName() {
        boolean multiSourceEnabled = jobConf.get(CommonOptions.MULTI_SOURCE_ENABLED);
        if (multiSourceEnabled) {
            Map<String, String> connectorConf = jobConf.getUnNecessaryMap(BaseMessageQueueReaderOptions.CONNECTOR_PROPERTIES);
            return super.geTransformerName() + "_" + connectorConf.get(KafkaConstants.CONNECTOR_SOURCE_INDEX);
        } else {
            return super.geTransformerName();
        }
    }


    /**
     * @param eventsCommon
     * @return tuple2
     */
    private Tuple2<String, String> getFinalDeviceIdTypes(EventsCommon eventsCommon) {
        String deviceId = "";
        String deviceType = "";
        if (!StringUtils.isNullOrWhitespaceOnly(eventsCommon.getUdid())) {
            deviceId = eventsCommon.getUdid();
            deviceType = "9";
        } else if (!StringUtils.isNullOrWhitespaceOnly(eventsCommon.getImei())) {
            deviceId = eventsCommon.getImei();
            deviceType = "0";
        } else if (!StringUtils.isNullOrWhitespaceOnly(eventsCommon.getSn())) {
            deviceId = eventsCommon.getSn();
            deviceType = "8";
        } else if (!StringUtils.isNullOrWhitespaceOnly(eventsCommon.getUuid())) {
            deviceId = eventsCommon.getUuid();
            deviceType = "6";
        }
        return Tuple2.of(deviceId, deviceType);
    }
}

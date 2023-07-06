package com.hihonor.datacollector.realtime.transform;

import com.alibaba.fastjson.JSONObject;
import com.bytedance.bitsail.base.execution.ExecutionEnviron;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.common.option.CommonOptions;
import com.bytedance.bitsail.connector.legacy.jdbc.constants.JdbcConstants;
import com.bytedance.bitsail.connector.legacy.messagequeue.source.option.BaseMessageQueueReaderOptions;
import com.bytedance.bitsail.flink.core.transformer.FlinkDataTransformerDAGBuilder;
import com.hihonor.datacollector.realtime.utils.GsonUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MysqlBinlogTransformDAGBuilder extends FlinkDataTransformerDAGBuilder<String, Row> {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlBinlogTransformDAGBuilder.class);

    private List<String> fieldNames;
    private String pkName;// primary key

    @Override
    public void configure(ExecutionEnviron execution,
                          BitSailConfiguration transformConfiguration) throws Exception {
        super.configure(execution, transformConfiguration);
        this.jobConf.merge(transformConfiguration, true, new Function[0]);
        Config config = ConfigFactory.load("conf/cdc_tb_push_register_table_names");
        fieldNames = config.getStringList("fieldnames");
        pkName = config.getString("pk");
    }


    @Override
    public DataStream<Row> addDiffTransform(DataStream<String> dataStream, int readerParallelism) throws Exception {
        return dataStream.flatMap(new FlatMapFunction<String, Row>() {
            @Override
            public void flatMap(String row, Collector<Row> collector) throws Exception {
                // mysql binlog json
                JSONObject jsonObject = GsonUtil.fromJson(row, JSONObject.class);
                JSONObject before = jsonObject.getJSONObject("before");
                JSONObject after = jsonObject.getJSONObject("after");
                String type = jsonObject.getString("binlog_type");
                Long ts = jsonObject.getLong("ts_ms");
                String key = "";
                List<String> inputs = new ArrayList<>();
                if ("delete".equalsIgnoreCase(type)) {
                    key = before.getString(pkName);
                    fieldNames.stream().forEach(x -> inputs.add(before.getString(x)));
                } else {
                    key = after.getString(pkName);
                    fieldNames.stream().forEach(x -> inputs.add(after.getString(x)));
                }
                inputs.add(type);
                inputs.add(String.valueOf(ts));
                String partition = (String) jsonObject.getString("tableName");
                String input = StringUtils.join(inputs, "\u0001");
                Row output = Row.of(key, input.getBytes(StandardCharsets.UTF_8), partition, 0, partition);
                collector.collect(output);
            }
        }).uid(geTransformerName()).name(geTransformerName()).setParallelism(readerParallelism);
    }

    @Override
    public DataStream<String> addSameTransform(DataStream<String> dataStream, int i) throws Exception {
        return null;
    }

    @Override
    public String geTransformerName() {
        boolean multiSourceEnabled = jobConf.get(CommonOptions.MULTI_SOURCE_ENABLED);
        if (multiSourceEnabled) {
            Map<String, String> connectorConf = jobConf.getUnNecessaryMap(BaseMessageQueueReaderOptions.CONNECTOR_PROPERTIES);
            return super.geTransformerName() + "_" + connectorConf.get(JdbcConstants.CONNECTOR_SOURCE_CDC_SERVER_ID);
        } else {
            return super.geTransformerName();
        }
    }
}

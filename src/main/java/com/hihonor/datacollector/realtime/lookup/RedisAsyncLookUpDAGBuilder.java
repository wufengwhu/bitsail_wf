package com.hihonor.datacollector.realtime.lookup;

import com.bytedance.bitsail.base.execution.ExecutionEnviron;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.connector.legacy.converter.AbstractRowConverter;
import com.bytedance.bitsail.connector.legacy.converter.RedisAsyncRowConverter;
import com.bytedance.bitsail.connector.legacy.lookup.config.LookupConfig;
import com.bytedance.bitsail.connector.legacy.lookup.config.LookupConfigFactory;
import com.bytedance.bitsail.connector.legacy.lookup.options.LookupOptions;
import com.bytedance.bitsail.connector.legacy.redis.config.RedisConfig;
import com.bytedance.bitsail.connector.legacy.redis.enums.RedisConnectType;
import com.bytedance.bitsail.connector.legacy.redis.enums.RedisDataMode;
import com.bytedance.bitsail.connector.legacy.redis.enums.RedisDataType;
import com.bytedance.bitsail.connector.legacy.redis.lookup.RedisLruLookUpFunction;
import com.bytedance.bitsail.flink.core.lookup.FlinkDataAsyncLookUpDAGBuilder;
import com.google.gson.reflect.TypeToken;
import com.hihonor.datacollector.realtime.entity.OutPutTables;
import com.hihonor.datacollector.realtime.utils.GsonUtil;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hihonor.datacollector.realtime.utils.GsonUtil.getGson;

public class RedisAsyncLookUpDAGBuilder extends FlinkDataAsyncLookUpDAGBuilder<Row> {

    private RedisLruLookUpFunction redisLruLookUpFunction;

    private LookupConfig lookupConfig;

    private OutPutTables outPutTables;

    @Override
    public void configure(ExecutionEnviron execution,
                          BitSailConfiguration lookupConfiguration) throws Exception {
        super.configure(execution, lookupConfiguration);
        Map<String, String> connectorConf = jobConf.getUnNecessaryMap(LookupOptions.CONNECTOR_PROPERTIES);
        List<Integer> keyIndexes = jobConf.get(LookupOptions.KEY_INDEX_IN_ROW);
        String keyPrefix = jobConf.get(LookupOptions.REDIS_KEY_PREFIX);
        Integer valueIndex = jobConf.get(LookupOptions.VALUE_INDEX_IN_ROW);

        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setHostPort(connectorConf.get("url"));
        redisConfig.setPassword(connectorConf.get("password"));
        redisConfig.setMode(RedisDataMode.getDataMode(connectorConf.get("data-read-mode")));
        redisConfig.setType(RedisDataType.getDataType(connectorConf.get("data-type")));
        redisConfig.setRedisConnectType(RedisConnectType.parse(Integer.valueOf(connectorConf.get("connect-type"))));
        redisConfig.setKeyIndexes(keyIndexes);
        redisConfig.setKeyPrefix(keyPrefix);

        Map<String, Object> objectMap =
                ConfigFactory.load("conf/repair_event.json").getObject("tableInfos").unwrapped();

        outPutTables = GsonUtil.fromJson(getGson().toJson(objectMap),
                new TypeToken<OutPutTables>() {
                }.getType());


        AbstractRowConverter rowConverter = new RedisAsyncRowConverter(valueIndex);
        lookupConfig = LookupConfigFactory.createLookupConfig(lookupConfiguration);
        redisLruLookUpFunction = new RedisLruLookUpFunction(redisConfig, lookupConfig, rowConverter);
    }

    @Override
    public DataStream<Row> addAsyncLookUp(DataStream<Row> input, int readerParallelism) throws Exception {
        return AsyncDataStream.unorderedWait(input, redisLruLookUpFunction, lookupConfig.getAsyncTimeout(), TimeUnit.MICROSECONDS)
                .uid(getLookUpName()).name(getLookUpName()).setParallelism(readerParallelism);
    }
}

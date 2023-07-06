package com.bytedance.bitsail.connector.legacy.converter;

import com.hihonor.datacollector.realtime.utils.GsonUtil;
import org.apache.flink.types.Row;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.bytedance.bitsail.flink.core.serialization.AbstractDeserializationSchema.DUMP_ROW_VALUE_INDEX;

public class RedisAsyncRowConverter extends AbstractRowConverter<Row, Object, Row, Row> {

    private static Long EXISTS = 1L;

    private String[] jsonLocationList;

    private int valueInRowIndex;

    public RedisAsyncRowConverter(int valueInRowIndex) {
        this.valueInRowIndex = valueInRowIndex;
    }

    @Override
    public Row toInternal(Row input) throws Exception {
        return null;
    }

    @Override
    public Row toInternalLookup(Row input, Object lookup) throws Exception {
        Long exists = (Long) lookup;
        String lookupValues = new String((byte[]) input.getField(DUMP_ROW_VALUE_INDEX), StandardCharsets.UTF_8);
        if (exists.compareTo(EXISTS) == 0) {
            String[] fields = lookupValues.split("\t");
            Map<String, Object> properties = GsonUtil.fromJson(fields[valueInRowIndex], HashMap.class);
            // 设置hdc 埋点元数据上下线校验标记
            properties.put("hdc_check_online", 1);
            fields[valueInRowIndex] = GsonUtil.mapToJson(properties);
            lookupValues = String.join("\t", fields);
        }
        Row lookUpOutputRow = Row.copy(input);
        lookUpOutputRow.setField(DUMP_ROW_VALUE_INDEX, lookupValues.getBytes(StandardCharsets.UTF_8));
        return lookUpOutputRow;
    }


    @Override
    public Row toExternal(Row rowData, Row output) throws Exception {
        return null;
    }
}

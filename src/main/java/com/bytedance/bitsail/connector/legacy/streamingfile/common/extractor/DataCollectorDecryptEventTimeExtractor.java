package com.bytedance.bitsail.connector.legacy.streamingfile.common.extractor;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DataCollectorDecryptEventTimeExtractor implements EventTimeExtractor {

    private int eventTimeIndex;

    public DataCollectorDecryptEventTimeExtractor(int eventTimeIndex) {
        this.eventTimeIndex = eventTimeIndex;
    }

    @Override
    public Object parse(byte[] record) throws Exception {
        String[] fields = new String(record, StandardCharsets.UTF_8).split("\t");
        return fields;
    }

    @Override
    public long extract(Object record) {
        // 取传进来的event time index, 其次当前时间戳
        String[] fields = ((String[]) record);
        String eventTime = Optional.ofNullable(fields[eventTimeIndex]).orElse(String.valueOf(System.currentTimeMillis()));
        return Long.parseLong(eventTime);
    }

    @Override
    public String getField(Object record, String fieldName, String defaultValue) throws Exception {
        String[] fields = (String[]) record;
        return null;
    }
}

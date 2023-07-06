package com.bytedance.bitsail.connector.legacy.streamingfile.common.extractor;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class DataCollectorEncryptEventTimeExtractor implements EventTimeExtractor {

    private int eventTimeIndex;

    public DataCollectorEncryptEventTimeExtractor(int eventTimeIndex) {
        this.eventTimeIndex = eventTimeIndex;
    }

    @Override
    public Object parse(byte[] record) throws Exception {
        String[] fields = new String(record, StandardCharsets.UTF_8).split("\u0001");
        return fields;
    }

    @Override
    public long extract(Object record) {
        // 取传进来的event time index, 其次当前时间戳
        String[] fields = ((String[]) record);
        long eventTime = 0L;
        long currentTime = System.currentTimeMillis();
        if (null != fields[eventTimeIndex]) {
            try {
                //延时超半小时,取flink 摄入当前时间戳
                long fieldEventTime = Long.parseLong(fields[eventTimeIndex]);
                eventTime = Math.abs(fieldEventTime - currentTime) >= 30 * 60 * 1000L ? currentTime : fieldEventTime;
            } catch (NumberFormatException e) {
                log.error("can not parse {} as timestamp", fields[eventTimeIndex]);
                eventTime = currentTime;
            }
        } else {
            eventTime = currentTime;
        }
        return eventTime;
    }

    @Override
    public String getField(Object record, String fieldName, String defaultValue) throws Exception {
        String[] fields = (String[]) record;
        return null;
    }
}

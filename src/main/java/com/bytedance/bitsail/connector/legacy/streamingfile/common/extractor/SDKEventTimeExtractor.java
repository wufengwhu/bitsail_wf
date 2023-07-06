package com.bytedance.bitsail.connector.legacy.streamingfile.common.extractor;

import com.hihonor.datacollector.realtime.entity.SDKEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class SDKEventTimeExtractor implements EventTimeExtractor {

    private String eventTimeFieldName;

    public SDKEventTimeExtractor(String eventTimeIndex) {
        this.eventTimeFieldName = eventTimeIndex;
    }

    @Override
    public Object parse(byte[] record) throws Exception {
        return SerializationUtils.deserialize(record);
    }

    @Override
    public long extract(Object record) {
        // 取传进来的event time index, 其次当前时间戳
        SDKEntity sdkEntity = (SDKEntity) record;
        try {
            Field field = SDKEntity.class.getDeclaredField(eventTimeFieldName);
            field.setAccessible(true);
            return Optional.ofNullable(Long.parseLong((String) field.get(sdkEntity))).orElse(System.currentTimeMillis());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("no such field or illegal access ,check your event time field name {}", eventTimeFieldName);
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    @Override
    public String getField(Object record, String fieldName, String defaultValue) throws Exception {
        String[] fields = (String[]) record;
        return null;
    }
}

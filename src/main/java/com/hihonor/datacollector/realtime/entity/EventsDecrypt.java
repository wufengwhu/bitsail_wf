package com.hihonor.datacollector.realtime.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hihonor.datacollector.realtime.utils.GsonUtil;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * @author w00027882
 */
@Setter
@Getter
public class EventsDecrypt {

    private Events[] events;

    @SerializedName("events_common")
    @JsonProperty("events_common")
    private EventsCommon eventsCommon;


    public Events[] getEvents() {
        return events;
    }


    public EventsCommon getEventsCommon() {
        return eventsCommon;
    }


    public static Object getValueByField(Object obj, Field field) {
        if (obj == null || field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param obj 没有集合类型
     * @return
     */
    public static Map<String, Object> getGenericFieldValueMap(Object obj,
                                                              Map<String, Object> fieldObjectMap,
                                                              String parentFieldNamePrefix) {
        if (obj == null) {
            return null;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            field.setAccessible(true);
            SerializedName serializedName = field.getAnnotation(SerializedName.class);
            String name = null == serializedName ? field.getName() : serializedName.value();
            if (field.getGenericType() == String.class) {
                String key = Optional.ofNullable(parentFieldNamePrefix).orElse("") + name;
                fieldObjectMap.put(key, getValueByField(obj, field));
            } else {
                // obj 类型
                try {
                    String subObjectFieldNamePrefix = Optional.ofNullable(parentFieldNamePrefix).orElse("") + name + ".";
                    if ("events.properties.".equalsIgnoreCase(subObjectFieldNamePrefix)) {
                        // events.properties 业务自定义的事件属性,对应map类型, 单独存放一个原始的字符串
                        Map<String, Object> eventsProperties = (Map<String, Object>) getValueByField(obj, field);
                        fieldObjectMap.put("events.properties", GsonUtil.mapToJson(eventsProperties));
                        // map 里的元素全部放进 fieldObjectMap
                        eventsProperties.forEach((k, v) -> fieldObjectMap.put(subObjectFieldNamePrefix + k, v));
                    } else {
                        // java bean
                        getGenericFieldValueMap(getValueByField(obj, field), fieldObjectMap, subObjectFieldNamePrefix);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        return fieldObjectMap;
    }


    public static void main(String[] args) {
        String header = "{\"protocol_version\":\"1\",\"compress_mode\":\"1\",\"serviceid\":\"himaintqrt\",\"appid\":\"com.hihonor.iconnect\",\"hmac\":\"c93109aa729f310f02774b341273a4b69615cfc80db1c1412318e143807dcf41\",\"chifer\":\"492c791592ca1995f820859309d172bc70438c5a57d4297929f0f0134de6d963ff349283ab4b6c24db57522fe891218e3ff16306e256b49fa8d3699781ec3df764913d833addd63397523f1f9e7feff7bcd684e870afce8edabb7b463ec2fad7b3d90ee6df5f912e9ed0af7402497d27f9641f389f6c07156cf72fc2374aa8191bf5daa680466c3fcebd7e116cae381c01c530ff462d7a28fba7ca2915ce9f93885806a2fd955c6105f9ab7ee8de6ae345d0beda2c070896e022948d173bdb4b46fde600ef266bf539b0a2df5f2b065dce9b775990b13d78e507c5dd217c36f86b35564224b93dcc69db1469944eec2350150d3a7f07c02e5b7008e98d12216f\",\"timestamp\":\"1670547600569\",\"servicetag\":\"_default_config_tag\",\"requestid\":\"ff8ea4b3daa948cfac7b53643597faad\",\"key\":\"bc7b91ef4383af07d387a05c27e6666d\"}";

        Header header1 = GsonUtil.fromJson(header, new TypeToken<Header>() {
        }.getType());

        System.out.println(header1.toString());
    }
}

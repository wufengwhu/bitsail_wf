package com.hihonor.datacollector.realtime.utils;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 从解密之后的数据中获取对应属性字段的数值
 */
public class DataUtils {

    // 获取属性对应的数值,包括了2层以及多层级的数值信息
    public static String getData(JsonElement data, String fieldName, int index) {
        if (data == null || fieldName == null) {
            return StringUtils.EMPTY;
        }
        String[] fieldLength = fieldName.split("\\.");
        if (fieldLength == null) {
            return StringUtils.EMPTY;
        }
        if (fieldLength.length == 1) {
            return getStringForOneLevel(fieldLength, data);
        }
        if (fieldLength.length == 2) {
            return getStringValueForSecondValue(fieldLength, data);
        }
        if (fieldLength.length == 3) {
            return getStringValueForThridValue(fieldLength, data, index);
        }
        if (fieldLength.length == 4) {
            return getStringValueForForthValue(fieldLength, data, index);
        }
        return StringUtils.EMPTY;
    }

    private static String getStringValueForForthValue(String[] fieldLength, JsonElement data, int index) {
        assert fieldLength.length == 4;
        String[] thirdDepth = Arrays.stream(fieldLength).limit(3).toArray(String[]::new);
        String thirdValue = getStringValueForThridValue(thirdDepth, data, index);
        if (StringUtils.isEmpty(thirdValue)) {
            return StringUtils.EMPTY;
        }
        JsonParser jsonParser = new JsonParser();
        JsonElement parse = jsonParser.parse(thirdValue);
        if (parse instanceof JsonPrimitive) {
            return StringUtils.EMPTY;
        } else if (parse instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) parse;
            JsonElement jsonElement = jsonObject.get(fieldLength[3]);
            if (jsonElement == null) {
                return StringUtils.EMPTY;
            } else {
                return jsonElement.getAsString();
            }
        }
        return StringUtils.EMPTY;
    }

    private static String getStringValueForThridValue(String[] fieldLength, JsonElement data, int index) {
        assert fieldLength.length == 3;
        String[] secondDepth = Arrays.stream(fieldLength).limit(2).toArray(String[]::new);
        String secondValue = getStringValueForSecondValue(secondDepth, data);
        if (StringUtils.isEmpty(secondValue)) {
            return StringUtils.EMPTY;
        }
        JsonParser jsonParser = new JsonParser();
        JsonElement asJsonObject = jsonParser.parse(secondValue);
        // 第二层级的只能是json对象，出现异常的话返回对应的空字符串。
        if (asJsonObject instanceof JsonObject) {
            JsonObject value = (JsonObject) asJsonObject;
            JsonElement jsonElement1 = value.get(fieldLength[2]);
            if (jsonElement1 instanceof JsonArray) {
                return jsonElement1.getAsJsonArray().toString();
            } else if (jsonElement1 instanceof JsonObject) {
                return jsonElement1.getAsJsonObject().toString();
            } else if (jsonElement1 == null) {
                return StringUtils.EMPTY;
            } else {
                return jsonElement1.getAsString();
            }
        } else if (asJsonObject instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) asJsonObject;
            if (jsonArray.size() == 0) {
                return StringUtils.EMPTY;
            }
            assert index <= jsonArray.size();
            JsonElement jsonElement = jsonArray.get(index).getAsJsonObject().get(fieldLength[2]);
            if (jsonElement == null) {
                return StringUtils.EMPTY;
            } else if (jsonElement instanceof JsonObject) {
                return jsonElement.getAsJsonObject().toString();
            } else if (jsonElement instanceof JsonArray) {
                return jsonElement.getAsJsonArray().toString();
            }
            return jsonElement.getAsString();
        } else {
            return StringUtils.EMPTY;
        }
    }

    private static String getStringValueForSecondValue(String[] fieldLength, JsonElement data) {
        assert fieldLength.length == 2;
        JsonElement jsonElement = data.getAsJsonObject().get(fieldLength[0]);
        if (jsonElement == null) {
            return StringUtils.EMPTY;
        }
        JsonElement jsonElement1 = jsonElement.getAsJsonObject().get(fieldLength[1]);
        if (jsonElement1 == null) {
            return StringUtils.EMPTY;
        } else {
            //对应的是json对象的话，输出的是json对象的。
            if (jsonElement1 instanceof JsonObject) {
                return jsonElement1.getAsJsonObject().toString();
            } else if (jsonElement1 instanceof JsonArray) {
                return jsonElement1.getAsJsonArray().toString();
            } else {
                return jsonElement1.getAsString();
            }
        }
    }

    private static String getStringForOneLevel(String[] fieldLength, JsonElement data) {
        assert fieldLength.length == 1;
        String fieldName = fieldLength[0];
        JsonElement jsonElement = data.getAsJsonObject().get(fieldName);
        if (jsonElement == null) {
            return StringUtils.EMPTY;
        } else if (jsonElement instanceof JsonObject) {
            return jsonElement.getAsJsonObject().toString();
        }
        return jsonElement.getAsString();
    }

    public static String getSpecialValueForDeviceInfo(JsonElement data, List<String> fieldsForDeviceInfo) {
        // 需要处理一下数据问题
        String destValue = StringUtils.EMPTY;
        if (fieldsForDeviceInfo == null) {
            return StringUtils.EMPTY;
        } else {
            int count = 0;
            for (int i = 0; i < fieldsForDeviceInfo.size(); i++) {
                String fieldName = fieldsForDeviceInfo.get(i);
                String data1 = getData(data, fieldName, 0);
                if (!StringUtils.isEmpty(data1)) {
                    switch (count) {
                        case 0:
                            destValue = data1 + "\001" + 0;
                            break;
                        case 1:
                            destValue = data1 + "\001" + 6;
                            break;
                        case 2:
                            destValue = data1 + "\001" + 8;
                            break;
                        case 3:
                            destValue = data1 + "\001" + 9;
                            break;
                        default:
                            destValue = StringUtils.EMPTY;
                            break;
                    }
                }
                if (!StringUtils.isEmpty(destValue)) {
                    break;
                }
            }
        }
        return destValue;
    }

    /**
     * 获取访问时间数据信息
     */
    public static String getAccessPageTime(JsonElement data, List<String> accessTimeFields) {
        if (accessTimeFields == null || accessTimeFields.size() == 0) {
            return StringUtils.EMPTY;
        } else {
            assert accessTimeFields.size() == 2;
            String eventTime = getData(data, accessTimeFields.get(0), 0);
            String eventDuration = getData(data, accessTimeFields.get(1), 0);
            String time = getTime(eventTime, eventDuration);
            return time;
        }
    }

    private static String getTime(String eventTime, String eventDuration) {
        Long start = 0L;
        Long end = 0L;
        if (StringUtils.isEmpty(eventDuration)) {
            end = 0L;
        } else {
            end = Long.parseLong(eventDuration);
        }
        if (StringUtils.isEmpty(eventTime)) {
            start = 0L;
        } else {
            start = Long.parseLong(eventTime);
        }
        Long duration = start - end;
        return duration.toString();
    }
}

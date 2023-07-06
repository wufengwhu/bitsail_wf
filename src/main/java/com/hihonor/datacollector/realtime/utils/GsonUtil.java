package com.hihonor.datacollector.realtime.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author w00027882
 */
public class GsonUtil {

    static GsonBuilder gsonBuilder = null;

    static {
        gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gsonBuilder.setLenient();
    }

    public static Gson getGson() {
        return gsonBuilder.create();
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        return getGson().fromJson(json, cls);
    }

    public static <T> T fromJson(String json, Type type) {
        return getGson().fromJson(json, type);
    }

    public static String mapToJson(Map<String, Object> objectMap) {
        return getGson().toJson(objectMap);
    }
}


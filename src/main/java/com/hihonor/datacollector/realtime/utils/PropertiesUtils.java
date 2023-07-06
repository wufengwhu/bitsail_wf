package com.hihonor.datacollector.realtime.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class PropertiesUtils {
    private static final Logger LOG = Logger.getLogger(PropertiesUtils.class);


    //加载tag相关的配置数据信息
    public static Map<Object, Object> loadTagProperties() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.hihonor.datacollector.realtime.config.tag", new Locale("zh", "CN"));
        StringBuilder sb = new StringBuilder();

        Map map = convertResourceBundleToMap(bundle);
        return map;
    }

    private static Map<String, Object> convertResourceBundleToMap(ResourceBundle resource) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = resource.getString(key);
            JSONArray jsonArray = new JSONArray(Collections.singletonList(value));
            map.put(key, jsonArray);
        }
        return map;
    }

    public static String[] getSelectedFields(Properties tag1) {
        if (tag1 == null || tag1.isEmpty()) {
            return null;
        }
        return Arrays.stream(tag1.getProperty("jsonLocationList").split(",")).toArray(String[]::new);
    }

    public static String[] fields(Properties tag1) {
        if (tag1 == null || tag1.isEmpty()) {
            return null;
        }
        return Arrays.stream(tag1.getProperty("fieldList").split(",")).toArray(String[]::new);
    }

    public static String[] getFieldsForDeviceInfo(Properties tag1) {
        if (tag1 == null || tag1.isEmpty()) {
            return null;
        }
        return Arrays.stream(tag1.getProperty("deviceId").split(",")).toArray(String[]::new);
    }

    //根据tag信息得到对应的tag的属性的对应关系
    public static Properties getTagProps(String tag) {
        Map properties = loadTagProperties();
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        Iterator<Map.Entry<Object, Object>> iterator = entries.iterator();
        Properties prefixProps = new Properties();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> nextValue = iterator.next();
            String key = (String) nextValue.getKey();
            JSONArray value = (JSONArray) nextValue.getValue();
            if (StringUtils.equals(tag, key)) {
                JSONObject destValue = (JSONObject) value.get(0);
                Iterator<Map.Entry<String, Object>> iter = destValue.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Object> next = iter.next();
                    prefixProps.put(next.getKey(), next.getValue());
                }
            }
        }
        return prefixProps;
    }
}

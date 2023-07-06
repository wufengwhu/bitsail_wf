package com.hihonor.datacollector.realtime.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

//加载通用的kafka的配置数据信息
public class LoadKafkaConfig {
    public static void main(String[] args) {
        Properties a = loadCommonInputConfig();
        System.out.println(a);
    }

    private static final Logger LOG = Logger.getLogger(LoadKafkaConfig.class);

    public static Properties loadCommonInputConfig() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.hihonor.datacollector.realtime.config.kafkaInput", new Locale("zh", "CN"));
        Properties inuptProp = new Properties();
        inuptProp.setProperty("topic_input", bundle.getString("topic_input"));
        inuptProp.setProperty("group.id", bundle.getString("group.id"));
        inuptProp.setProperty("bootstrap.servers", bundle.getString("bootstrap.servers"));
        inuptProp.setProperty("key.deserializer", bundle.getString("key.deserializer"));
        inuptProp.setProperty("value.deserializer", bundle.getString("value.deserializer"));
        return inuptProp;
    }
}

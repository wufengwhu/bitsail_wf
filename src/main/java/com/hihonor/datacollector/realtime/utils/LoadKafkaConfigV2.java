package com.hihonor.datacollector.realtime.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

//加载通用的kafka的配置数据信息
public class LoadKafkaConfigV2 {
    private static final Logger LOG = Logger.getLogger(LoadKafkaConfigV2.class);


    public static Properties loadCommonOutPutConfig() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.hihonor.datacollector.realtime.config.kafkaOutput", new Locale("zh", "CN"));
        Properties outputProp = new Properties();
        outputProp.setProperty("topic_output", bundle.getString("topic_output"));
        outputProp.setProperty("group.id", bundle.getString("group.id"));
        outputProp.setProperty("bootstrap.servers", bundle.getString("bootstrap.servers"));
        outputProp.setProperty("key.deserializer", bundle.getString("key.deserializer"));
        outputProp.setProperty("value.deserializer", bundle.getString("value.deserializer"));
        return outputProp;
    }
}

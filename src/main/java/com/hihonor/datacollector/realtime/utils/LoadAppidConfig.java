package com.hihonor.datacollector.realtime.utils;

import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

//加载通用的kafka的配置数据信息
public class LoadAppidConfig {
    public static void main(String[] args) {
        Properties a = loadAppidConfig();
        System.out.println(a);
    }

    private static final Logger LOG = Logger.getLogger(LoadAppidConfig.class);

    public static Properties loadAppidConfig() {
        ResourceBundle bundle = ResourceBundle.getBundle("com.hihonor.datacollector.realtime.config.appidList", new Locale("zh", "CN"));
        Properties inuptProp = new Properties();
        inuptProp.setProperty("appid", bundle.getString("appid"));
        return inuptProp;
    }
}

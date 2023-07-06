package com.hihonor.datacollector.realtime.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.hihonor.datacollector.realtime.crypto.utils.ABClass;
import com.hihonor.datacollector.realtime.udf.AESDecryptHAUDF;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述 数据解密兵转为相应的格式
 *
 * @author g00021929
 * @since 2022/1/7
 */
public class DataDecryptAndFormatUtils {
    private static final Logger LOG = Logger.getLogger(DataDecryptAndFormatUtils.class);

    //数据的分段解密
    public static List<ABClass> transFormDataV2(String value, Properties tagProps, String appid) {
        //根据属性以及对应的对应的数据属性来完成相关的属性的组装操作实现
        if (StringUtils.isEmpty(value)) {
            LOG.error("待解密数据为空");
            return null;
        }
        String[] values = value.split("\001");
        if (values.length != 3) {
            LOG.error("数据格式不符合规范");
            return null;
        }
        String dataDecrypt = StringUtils.EMPTY;
        if (values[0] != null && StringUtils.isNotEmpty(values[0]) && !StringUtils.equals("null", values[0]) && judgeAppid(values[0], appid)) {
            dataDecrypt = AESDecryptHAUDF.decryptDataBySDK(values[0]);
        }
        if (StringUtils.isEmpty(dataDecrypt)) {
            LOG.error("待解密的数据为空,无法完成后续的数据解密操作");
        }
        return dealWithDataV2(dataDecrypt, tagProps);
    }

    //真正的数据处理的逻辑,测试代码逻辑
    private static List<ABClass> dealWithDataV2(String encryData, Properties tag2Props) {
        String[] selectedFields = PropertiesUtils.getSelectedFields(tag2Props);//该部分是输入的
        String[] fields = PropertiesUtils.fields(tag2Props);//改成ab的名字

        if (selectedFields == null || selectedFields.length == 0) {
            LOG.error("input selectedFields is empty");
            return null;
        }
        if (fields == null || fields.length == 0) {
            LOG.error("input fields is empty");
            return null;
        }
        List<ABClass> rowsOfABtest = new ArrayList<>();
        ABClass abClass = new ABClass();
        if (StringUtils.isNotEmpty(encryData)) {
            JsonParser parser = new JsonParser();
            try {
                JsonElement data = parser.parse(encryData);
                String eventsValue = DataUtils.getData(data, "eventDe.events", 0);
                if (eventsValue == null || StringUtils.isEmpty(eventsValue)) {
                    rowsOfABtest.add(abClass);
                } else {
                    JsonElement parse = parser.parse(eventsValue);
                    if (parse != null && parse instanceof JsonArray) {
                        JsonArray jsonArray = (JsonArray) parse;
                        for (int i = 0; i < jsonArray.size(); i++) {
                            List<ABClass> abClassListTemp = transformDatawithFieldsV2(selectedFields, i, data, fields);
                            if (abClassListTemp != null && abClassListTemp.size() > 0) {
                                rowsOfABtest.addAll(abClassListTemp);
                            }
                        }
                    }
                }
            } catch (JsonSyntaxException e) {
                LOG.error("=======encryData exception========" + encryData);
            }
        }
        return rowsOfABtest;
    }

    private static List<ABClass> transformDatawithFieldsV2(String[] selectedFields, int index, JsonElement data, String[] fields) {
        assert selectedFields.length > 0;
        List<ABClass> abClasses = new ArrayList<>();
        Map<String, String> values = new TreeMap<>();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = selectedFields[i];
            String fieldValue = DataUtils.getData(data, fieldName, index);
            String field = fields[i];
            values.put(field, fieldValue);
        }
        //values.replace("strategies", "123321");
        //如果没有加入策略ID，则不需要给到AB平台
        if (StringUtils.isEmpty(values.get("strategies"))) {
            return null;
        }

        ABClass abClass = new ABClass();
        try {
            BeanUtils.populate(abClass, values);
            abClass.setEventTime(stampToDate(abClass.getEventTime()));
            abClasses.add(abClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Map  to bean exception  " + e.getMessage());
            return null;
        }
        return abClasses;
    }

    /**
     * 获取字段的day信息
     */
    public static String getDateFromTimestamp(String timeStamp) {
        long time = Long.parseLong(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return year + "-" + month + "-" + day;
    }

    /**
     * 获取字段的day信息
     */
    public static String getHourFromTimestamp(String timeStamp) {
        long time = Long.parseLong(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int day = calendar.get(Calendar.HOUR_OF_DAY);
        return day + "";
    }

    /**
     * 时间戳转时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 判断业务是否在白名单当中
     *
     * @param input 输入字符串
     * @param appid appid白名单
     * @return true or false
     */
    public static boolean judgeAppid(String input, String appid) {
        String[] splits = input.split(",");
        if (splits.length>4){
            String appidStr = splits[3];
            int length = appidStr.length();
            String appidGet = appidStr.substring(9, length - 1);

            String[] appids = appid.split(",");
            return ArrayUtils.contains(appids, appidGet);
        }
        else {
            return false;
        }

    }
}

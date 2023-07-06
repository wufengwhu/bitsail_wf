/*
Copyright: (c) Honor Technologies Co., Ltd. 2022. All rights reserved.
 */
package com.hihonor.datacollector.realtime.udf;

/**
 * 功能描述
 *
 * @author g00021929
 * @since 2021-01-05
 */

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hihonor.datacollector.realtime.crypto.encryptor.AESCryptorBase64;
import com.hihonor.datacollector.realtime.crypto.encryptor.RsaCryptor;
import com.hihonor.datacollector.realtime.crypto.utils.AESConfig;
import com.hihonor.datacollector.realtime.crypto.utils.Common;
import com.hihonor.datacollector.realtime.crypto.utils.HexUtils;
import com.hihonor.datacollector.realtime.crypto.utils.InflaterUtil;
import com.hihonor.datacollector.realtime.entity.EventsDecrypt;
import com.hihonor.datacollector.realtime.utils.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hihonor.datacollector.realtime.crypto.encryptor.AESCryptorBase64.UTF_8;

public class AESDecryptHAUDF {
    private static final String decryptWorkKey = AESConfig.getSecretKey(Common.sdk_workKey_key);

    final static String[] ALONE_PACKAGE_NAME_ARRAY = new String[]{"MAGIC.Router.Router", "com.hihonor.android.totemweather",
            "com.hihonor.audioassistant"};

    public static final Pattern PATTERN = Pattern.compile("\\*|\t|\r|\n");

    private static final Logger LOG = LoggerFactory.getLogger(AESDecryptHAUDF.class);


    public static String decryptDataBySDK(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        JsonObject jsonObject = new Gson().fromJson(value, new TypeToken<JsonObject>() {
        }.getType());

        JsonObject header = jsonObject.getAsJsonObject("header");
        JsonElement chifer = header.get("chifer");
        String keyEn = chifer.getAsString();
        String eventEn = jsonObject.get("event").getAsString();
        try {
            String key = RsaCryptor.rsaDecrypt(keyEn);
            header.addProperty("key", key);
            byte[] bytes = AESCryptorBase64.decryptData(HexUtils.toBytes(eventEn), HexUtils.toBytes(key));
            String s = new String(InflaterUtil.decompress(bytes), UTF_8);
            Matcher m = PATTERN.matcher(s);
            String event = m.replaceAll("");
            jsonObject.addProperty("eventDe", event.replaceAll("\\\\", "#")
                    .replace("\"{", "$_start").replace("}\"", "$_end"));
            String jsonObjectAsString = new Gson().toJson(jsonObject).replaceAll("\\\\", "");
            String replace = jsonObjectAsString.replace("\"{", "{").replace("}\"", "}");
            return replace.replaceAll("#", "\\\\").replace("$_start", "\"{").replace("$_end", "}\"");
        } catch (Exception e) {
            return null;
        }
    }

    public static EventsDecrypt decryptEvents(JsonObject jsonObject) {
        byte[] bytes = null;
        try {
            JsonObject header = jsonObject.getAsJsonObject("header");
            String appid = header.get("appid").getAsString();
            Asserts.notNull(appid, "appid is null");
            JsonElement chifer = header.get("chifer");
            String keyEn = chifer.getAsString();
            String eventEn = jsonObject.get("event").getAsString();
            String key = null;
            if (keyEn != null && !keyEn.isEmpty() && !Arrays.asList(ALONE_PACKAGE_NAME_ARRAY).contains(appid)) {
                key = RsaCryptor.rsaDecrypt(keyEn);
                header.addProperty("key", key);
                bytes = AESCryptorBase64.decryptData(HexUtils.toBytes(eventEn), HexUtils.toBytes(key));
                String event = new String(InflaterUtil.decompress(bytes), UTF_8);
                Matcher m = PATTERN.matcher(event);
                String eventDe = m.replaceAll("");

                return GsonUtil.fromJson(eventDe,
                        new TypeToken<EventsDecrypt>() {
                        }.getType());
            } else {
                LOG.info("chifer is null");
                LOG.error(jsonObject.toString());
                return null;
            }
        } catch (Exception e) {
            LOG.error(jsonObject.toString());
            LOG.error(String.valueOf(bytes));
            e.printStackTrace();
        }
        return null;
    }


    public static String decryptIPBySDK(String value) {
        try {
            byte[] bytes = AESCryptorBase64.decryptData(HexUtils.toBytes(value), HexUtils.toBytes(decryptWorkKey));
            return new String(bytes, UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String testValue = "{\"header\":{\"protocol_version\":\"1\",\"compress_mode\":\"1\",\"serviceid\":\"hmshioperqrt\",\"appid\":\"com.hihonor.experience.jlh\",\"hmac\":\"c21da04d469026187d5ab0708f2d5a3a3f23286b9270854875b79bca8467fbfe\",\"chifer\":\"8cd9af51bc89f2392a600f40f6680d6ae5b6a9208c1e251e0b619c2d3dc725801effbcc4f4e2d5c901914ec7cf9c5e34db4342797b4de8dbcf3a398be472a047d457a9790a2c01e5f250578b02b601407d9dc96b03192cfdd3241bcfd4e8cea62ea548e65cf286e4e2bb834a0e8e5ca48bef8e11b5c734126c48e4e53a1320a39bfa2473f854849dc87936c2d305d6bcdc3606272957edb7488063c8a5e8fec4fd024bd4140ab3d5f22b168a1f1cd25815dbc9fa5ac16c1348d0670d75548f3ec6593a2da5b68caff841e045d6f069e1b366b3a71ba1de20f40451ff27023a467e4f7e58d627322813c5fab5f7bd59d85835234e5fc193853f2042e4ccf0a645\",\"timestamp\":\"1623895331341\",\"servicetag\":\"_default_config_tag\",\"requestid\":\"c319e01a3380445c8bdee8ea0d2176fe\"},\"event\":\"a0fc0781a525cd4b7f4360ea1a8e97ceaec725f51f3169c5bfbc26e613ed0c98dee54d1dd67eb6c14270bc5a18db83971b4f5779edfc3e74cad54162b170d6a5d8a19ca0129915424c6daabd23c7a1f4e2593a0a09cd1ed586962c48f513d9a452d06614eb8b6f36620b7ac07047e8b05912eda7fd329da9df6fe5f1f75ce10ab3e05064d997c1bea0fa7f38541fc33c60669b3588f5ca0f70234fb9732b329980825930d230c51927afc4e4d55d930a1b92374c0e015529d3d898f283cbd76371ad42a9e81fd3dc632d8ced864b1852bc6c774822c77ff09a8460aacefd2491fafaa8b116f99c82b5153544b3128c5cd58fcd514f8396b225a48f53a34b0816313e1facd8f6f7cda3953a10bd09f505a480504371f2d50b02e5f39170e63365914a1ebf0389408e4eb5719c3fe86535bfcb823dc6108552c33b741376c487a4a2fb11d1a0806a4c79b5a94702a538f4c5903d10293ccdbded2e5cd7208cc15bf90d32a4a61f356a63e1359e6f554e8363f992bd0fabd8fa9f7372632dc98727233eba3013173659426bb15c8ab5fb76429be4488cc9c086e14d30d09af732582ce613f79c7593623ae1693dec0134ea\"}";
//        System.out.println(decryptDataBySDK(testValue));
        System.out.println(decryptIPBySDK("9fec786cdbd65b4bd65d4e620a68c4f75f78547926a330dc3a81b57f26455843"));


    }
}

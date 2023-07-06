package com.hihonor.datacollector.realtime.crypto.utils;

import com.huawei.secure.crypto.codec.AegisDecoderException;
import com.huawei.secure.crypto.rootkey.RootKeyUtil;
import com.huawei.secure.crypto.workkey.WorkKeyCryptUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The Class AESConfig.
 */
public class AESConfig {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = Logger.getLogger(AESConfig.class);
    static RootKeyUtil rootKeyUtil = null;
    static ResourceBundle bundle = ResourceBundle.getBundle(Common.KEY_FILE, new Locale("zh", "CN"));
    static String first = bundle.getString(Common.pig_key);
    static String second = bundle.getString(Common.dog_key);
    static String third = bundle.getString(Common.duck_key);
    static String salt = bundle.getString(Common.cat_key);
    /**
     * The config.
     */
    private static Properties properties = null;
    /**
     * The ResourceBundle.
     */
    private static ResourceBundle resource = null;

    static {
        initResource();
    }

    /**
     * Gets the secret key.
     *
     * @param key the key
     * @return the secret key
     */
    public static String getSecretKeyByResource(String key) {
        if (resource == null) {
            return null;
        }
        return resource.getString(key);
    }

    /**
     * Inits the.
     */
    private static void initResource() {
        resource = ResourceBundle.getBundle("com.hihonor.datacollector.realtime.config.commonConfig");
    }

    /**
     * 生成加密秘钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSecretKey(String workKeyName) {
        String workKey = bundle.getString(workKeyName);
        String decryptWorkKey = null;
        try {
            rootKeyUtil = RootKeyUtil.newInstance256(first, second, third, salt);
            decryptWorkKey = WorkKeyCryptUtil.decryptWorkKey(workKey, rootKeyUtil);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AegisDecoderException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return decryptWorkKey;// 转换为AES专用密钥
    }
}

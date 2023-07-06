package com.hihonor.datacollector.realtime.crypto.encryptor;


import com.hihonor.datacollector.realtime.crypto.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Locale;
import java.util.ResourceBundle;

public class RsaCryptor {
    /**
     * 指定私钥存放文件
     */
    private static final String KEY_FILE = "com.hihonor.datacollector.realtime.config.commonConfig";
    /**
     * key
     */
    private static final String PRIVATE_KEY = "rsaPrivateKey";

    private static final String TAG = "RsaCryPter.";
    private static final Logger HiLog = LoggerFactory.getLogger(RsaCryptor.class);

//    /**
//     * rsaEncrypt, 对秘钥进行RSA2056加密
//     *
//     * @param key: 随机生成的密钥
//     * @return 加密后的密钥
//     */
//    public static String rasEncryptKey(String pubKey, String key) {
//        String rsaKey = "";
//        try {
//            rsaKey = rsaEncrypt(pubKey, key);
//        } catch (RSAEncryptionException e) {
//            HiLog.info(TAG + "rsaEncrypt(): Fail to encrypt with RSA!");
//        }
//        return rsaKey;
//    }
//
//    /**
//     * Encrypt text by RSA
//     *
//     * @param text plain text to encrypt
//     * @return encrypted text
//     */
//    private static String rsaEncrypt(String pubKey, String text) throws RSAEncryptionException {
//        try {
//            byte[] encryptedData = rsaEncrypt(pubKey, text.getBytes("UTF-8"));
//            return HexUtils.byteArray2HexString(encryptedData);
//        } catch (UnsupportedEncodingException e) {
//            HiLog.info(TAG + "rsaEncrypt(): Unsupported Encoding - utf-8!");
//            throw new RSAEncryptionException();
//        }
//    }
//
//    /**
//     * RSA加密过程
//     *
//     * @param plainTextData 明文数据
//     * @return 加密过程中的异常信息
//     */
//    private static byte[] rsaEncrypt(String pubKey, byte[] plainTextData) throws RSAEncryptionException {
//        try {
//            // 加载公钥
//            PublicKey publicKey = loadPublicKey2(pubKey);
//
//            if (null == publicKey) {
//                throw new UnsupportedEncodingException();
//            }
//            Cipher cipher;
//            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING", new BouncyCastleProvider());
//            //cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING",new SunJCE());
//            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING");
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            return cipher.doFinal(plainTextData);
//        } catch (UnsupportedEncodingException e) {
//            HiLog.info(TAG + "rsaEncrypt(): getBytes - Unsupported coding format!");
//            throw new RSAEncryptionException();
//        } catch (NoSuchAlgorithmException e) {
//            HiLog.info(TAG + "rsaEncrypt(): getInstance - No such algorithm,transformation");
//            throw new RSAEncryptionException();
//        } catch (InvalidKeyException e) {
//            HiLog.info(TAG + "rsaEncrypt(): init - Invalid key!");
//            throw new RSAEncryptionException();
//        } catch (NoSuchPaddingException e) {
//            HiLog.info(TAG + "rsaEncrypt():  No such filling parameters ");
//            throw new RSAEncryptionException();
//        } catch (BadPaddingException e) {
//            HiLog.info(TAG + "rsaEncrypt():False filling parameters!");
//            throw new RSAEncryptionException();
//        } catch (InvalidKeySpecException e) {
//            HiLog.info(TAG + "rsaEncrypt(): Invalid key specification");
//            throw new RSAEncryptionException();
//        } catch (IllegalBlockSizeException e) {
//            HiLog.info(TAG + "rsaEncrypt(): doFinal - The provided block is not filled with");
//            throw new RSAEncryptionException();
//        }
//    }

//    private static PublicKey loadPublicKey2(String rsaKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        PublicKey publicKey = null;
//        byte[] pubKeyBytes = HexUtils.hexString2ByteArray(rsaKey);
//        X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
//        // RSA非对称加密算法
//        KeyFactory keyFactory;
//        keyFactory = KeyFactory.getInstance("RSA");
//        // 取公钥匙对象
//        publicKey = keyFactory.generatePublic(bobPubKeySpec);
//        return publicKey;
//    }

    /**
     * RSA加密过程
     *
     * @param plainTextData 明文数据
     * @return 加密过程中的异常信息
     */
    public static byte[] rsaDecrypt(String privateKeyStr, byte[] plainTextData) throws RSAEncryptionException {
        try {

            //生成私钥
            PrivateKey privateKey = loadPrivateKey(privateKeyStr);
            //数据解密
            Cipher cipher;
            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//            HiLog.info("provider: {}", cipher.getProvider().getClass().getName());
            if (null == privateKey) {
                throw new UnsupportedEncodingException();
            }
            return cipher.doFinal(plainTextData);
        } catch (UnsupportedEncodingException e) {
            HiLog.error(TAG + "rsaEncrypt(): getBytes - Unsupported coding format!");
            throw new RSAEncryptionException();
        } catch (NoSuchAlgorithmException e) {
            HiLog.info(TAG + "rsaEncrypt(): getInstance - No such algorithm,transformation");
            throw new RSAEncryptionException();
        } catch (InvalidKeyException e) {
            HiLog.info(TAG + "rsaEncrypt(): init - Invalid key!");
            throw new RSAEncryptionException();
        } catch (NoSuchPaddingException e) {
            HiLog.info(TAG + "rsaEncrypt():  No such filling parameters ");
            throw new RSAEncryptionException();
        } catch (BadPaddingException e) {
            HiLog.info(TAG + "rsaEncrypt():False filling parameters!{}", e);
            throw new RSAEncryptionException();
        } catch (InvalidKeySpecException e) {
            HiLog.info(TAG + "rsaEncrypt(): Invalid key specification");
            throw new RSAEncryptionException();
        } catch (IllegalBlockSizeException e) {
            HiLog.info(TAG + "rsaEncrypt(): doFinal - The provided block is not filled with");
            throw new RSAEncryptionException();
        }
    }

    /**
     * Encrypt text by RSA
     *
     * @param text plain text to encrypt
     * @return encrypted text
     */
    public static String rsaDecrypt(String privateKeyStr, String text) throws RSAEncryptionException {
        byte[] encryptedData = rsaDecrypt(privateKeyStr, HexUtils.toBytes(text));
        return new String(encryptedData);

    }

    /**
     * Encrypt text by RSA
     *
     * @param text plain text to encrypt
     * @return encrypted text
     */
    public static String rsaDecrypt(String text) throws RSAEncryptionException {
        ResourceBundle bundle = ResourceBundle.getBundle(KEY_FILE, new Locale("zh", "CN"));
        String privateKeyStr = bundle.getString(PRIVATE_KEY);
        return rsaDecrypt(privateKeyStr, text);
    }

    private static PrivateKey loadPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {

        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(HexUtils.hexString2ByteArray(privateKeyStr));
        // RSA非对称加密算法
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        return privateKey;
    }

    public static class RSAEncryptionException extends Exception {
        public RSAEncryptionException() {
            super("Fail to encrypt with RSA");
        }
    }
}

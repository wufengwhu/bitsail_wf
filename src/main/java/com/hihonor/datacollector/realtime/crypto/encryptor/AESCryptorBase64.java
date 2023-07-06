package com.hihonor.datacollector.realtime.crypto.encryptor;

import com.hihonor.datacollector.realtime.crypto.utils.AESConfig;
import com.hihonor.datacollector.realtime.crypto.utils.Common;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class AESCryptorBase64.
 */
public class AESCryptorBase64 {
    /**
     * The Constant UTF_8.
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * The Constant AES_BLOCK_SIZE.
     */
    private static final int AES_BLOCK_SIZE = 16;

    /**
     * The Constant CRC_TB.
     */
    private static final char[] CRC_TB = {0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7, 0x8108,
            0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef, 0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294,
            0x72f7, 0x62d6, 0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de, 0x2462, 0x3443, 0x0420,
            0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485, 0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4, 0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df,
            0xe7fe, 0xd79d, 0xc7bc, 0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823, 0xc9cc, 0xd9ed,
            0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b, 0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33,
            0x2a12, 0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a, 0x6ca6, 0x7c87, 0x4ce4, 0x5cc5,
            0x2c22, 0x3c03, 0x0c60, 0x1c41, 0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49, 0x7e97,
            0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70, 0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a,
            0x9f59, 0x8f78, 0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f, 0x1080, 0x00a1, 0x30c2,
            0x20e3, 0x5004, 0x4025, 0x7046, 0x6067, 0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
            0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256, 0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e,
            0xe54f, 0xd52c, 0xc50d, 0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405, 0xa7db, 0xb7fa,
            0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c, 0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615,
            0x5634, 0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab, 0x5844, 0x4865, 0x7806, 0x6827,
            0x18c0, 0x08e1, 0x3882, 0x28a3, 0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a, 0x4a75,
            0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92, 0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b,
            0x9de8, 0x8dc9, 0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1, 0xef1f, 0xff3e, 0xcf5d,
            0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8, 0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0};

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = Logger.getLogger(AESCryptorBase64.class);

    /**
     * The Constant PATH_WHITE_LIST.
     */
    private static final String PATH_WHITE_LIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-=[];\\',./ ~!@#$%^&*()_+\"{}|:<>?";

    /**
     * The Constant PATTERN.
     */
    private static final Pattern PATTERN = Pattern
            .compile("(.*([/\\\\]{1}[\\.]{1,2}|[\\.]{1,2}[/\\\\]{1}|\\.\\.).*|\\.)");

    /**
     * Calu CRC.
     *
     * @param pByte the byte
     * @return the char
     */
    public static char caluCRC(byte[] pByte) {
        int len = pByte.length;
        char c;
        byte da;
        c = 0x0;
        int i = 0;
        while (len-- != 0) {
            da = (byte) (c >> 8);
            c <<= 8;
            int num = da ^ pByte[i];
            if (num < 0) {
                num += 256;
            }
            c ^= CRC_TB[num];
            ++i;
        }
        return c;
    }


    /**
     * Decrypt data.
     *
     * @param cipherTextWithIv the cipher text with iv
     * @param key              the key
     * @return the byte[]
     */
    @SuppressWarnings("nls")
    public static byte[] decryptData(byte[] cipherTextWithIv, byte[] key) {
        if (null == key || key.length != 16) {
            return new byte[0];
        }
        try {
            byte[] sizeArr = new byte[AES_BLOCK_SIZE];
            byte[] cipherText = new byte[cipherTextWithIv.length - AES_BLOCK_SIZE];
            System.arraycopy(cipherTextWithIv, 0, sizeArr, 0, AES_BLOCK_SIZE);
            System.arraycopy(cipherTextWithIv, AES_BLOCK_SIZE, cipherText, 0, cipherText.length);
            IvParameterSpec ivSpec = new IvParameterSpec(sizeArr);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(AESConfig.getSecretKeyByResource("aes.cbc.pkcs5padding"));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException ex) {
            LOG.error("DecryptData NoSuchAlgorithmException");
        } catch (InvalidKeyException ex) {
            LOG.error("DecryptData InvalidKeyException");
        } catch (NoSuchPaddingException ex) {
            LOG.error("DecryptData NoSuchPaddingException");
        } catch (InvalidAlgorithmParameterException ex) {
            LOG.error("DecryptData InvalidAlgorithmParameterException");
        } catch (BadPaddingException ex) {
            LOG.error("DecryptData BadPaddingException");
        } catch (IllegalBlockSizeException ex) {
            LOG.error("DecryptData IllegalBlockSizeException");
        }

        return new byte[0];
    }

    /**
     * Decrypt data.
     *
     * @param plainText the plain text
     * @param key       the key
     * @return the string
     */
    @SuppressWarnings("nls")
    public static String decryptData(String plainText, byte[] key) {
        try {
            byte[] decodeBase64 = Base64.decodeBase64(plainText);
            if (null != decodeBase64) {
                byte[] result = decryptData(decodeBase64, key);
                if (null != result) {
                    return new String(result, UTF_8);
                }
            }
        } catch (NegativeArraySizeException e) {
            LOG.error("DecryptData NegativeArraySizeException");
            return null;
        }

        return null;
    }

    /**
     * Decrypt data.
     *
     * @param plainText the plain text
     * @return the string
     */
    @SuppressWarnings("nls")
    public static String decryptDataByAesBase64(String plainText) {
        byte[] base64 = Base64.decodeBase64(plainText);
        String decryptWorkKey = AESConfig.getSecretKey(Common.psi_workKey_key);
        byte[] keyBytes = Base64.decodeBase64(decryptWorkKey);
        if (null != base64) {
            byte[] data = decryptData(base64, keyBytes);
            if (null != data) {
                return new String(data, UTF_8);
            }
        }
        return null;
    }

    /**
     * Encrypt data.
     *
     * @param plainText the plain text
     * @param key       the key
     * @return the byte[]
     */
    @SuppressWarnings("nls")
    public static byte[] encryptData(byte[] plainText, byte[] key) {
        return encryptData(plainText, key, getRandomBytes());
    }

    /**
     * Encrypt data.
     *
     * @param plainText the plain text
     * @param key       the key
     * @return the string
     */
    @SuppressWarnings("nls")
    public static String encryptData(String plainText, byte[] key) {
        byte[] bytes = encryptData(plainText.getBytes(UTF_8), key);
        // 64-bit encoding reduces the length of the next password
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Encrypt data.
     *
     * @param plainText the plain text
     *                  the key
     * @return the string
     */
    @SuppressWarnings("nls")
    public static String encryptData(String plainText) {
        String decryptWorkKey = AESConfig.getSecretKey(Common.psi_workKey_key);
        return encryptData(plainText, Base64.decodeBase64(decryptWorkKey));
    }

    /**
     * Encrypt data by same IV.
     *
     * @param plainText the plain text
     * @param key       the key
     * @return the string
     */
    @SuppressWarnings("nls")
    public static String encryptDataBySameIV(String plainText, String key) {
        byte[] data = encryptData(plainText.getBytes(UTF_8), Base64.decodeBase64(key), getSameInitVector());
        String res = Base64.encodeBase64String(data);
        return res.replaceAll("\r\n", "");
    }

    /**
     * Checks if is safe path.
     *
     * @param filePath the file path
     * @return true, if is safe path
     */
    public static boolean isSafePath(final String filePath) {
        Matcher match = PATTERN.matcher(filePath);
        boolean isSafe = !match.matches();
        return isSafe;
    }

    /**
     * Replace safe char.
     *
     * @param filePaths the file path
     * @return the string
     */
    public static String replaceSafeChar(final String filePaths) {

        if (StringUtils.isBlank(filePaths)) {
            return "";
        }

        StringBuilder tmpStrBuf = new StringBuilder(filePaths.length());
        for (int i = 0; i < filePaths.length(); i++) {
            for (int j = 0; j < PATH_WHITE_LIST.length(); j++) {
                if (filePaths.charAt(i) == PATH_WHITE_LIST.charAt(j)) {
                    tmpStrBuf.append(PATH_WHITE_LIST.charAt(j));
                    break;
                }
            }
        }

        return tmpStrBuf.toString();
    }

    /**
     * Encrypt data.
     *
     * @param plainText the plain text
     * @param key       the key
     * @param iv        the iv
     * @return the byte[]
     */
    @SuppressWarnings("nls")
    private static byte[] encryptData(byte[] plainText, byte[] key, byte[] iv) {
        if (null == key || key.length != 16) {
            return new byte[0];
        }

        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(AESConfig.getSecretKeyByResource("aes.cbc.pkcs5padding"));
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(plainText);
            byte[] cipherTextWithIv = new byte[cipherText.length + AES_BLOCK_SIZE];
            System.arraycopy(iv, 0, cipherTextWithIv, 0, AES_BLOCK_SIZE);
            System.arraycopy(cipherText, 0, cipherTextWithIv, AES_BLOCK_SIZE, cipherText.length);
            return cipherTextWithIv;
        } catch (NoSuchAlgorithmException ex) {
            LOG.error("EncryptData NoSuchAlgorithmException");
        } catch (NoSuchPaddingException ex) {
            LOG.error("EncryptData NoSuchPaddingException");
        } catch (InvalidKeyException ex) {
            LOG.error("EncryptData InvalidKeyException");
        } catch (InvalidAlgorithmParameterException ex) {
            LOG.error("EncryptData InvalidAlgorithmParameterException");
        } catch (IllegalBlockSizeException ex) {
            LOG.error("EncryptData IllegalBlockSizeException");
        } catch (BadPaddingException ex) {
            LOG.error("EncryptData BadPaddingException");
        }

        return new byte[0];
    }

    /**
     * Gets the random bytes.
     *
     * @return the random bytes
     */
    private static byte[] getRandomBytes() {
        byte[] bytes = new byte[AES_BLOCK_SIZE];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(bytes);
        return bytes;
    }

    /**
     * Gets the same init vector.
     *
     * @return the same init vector
     */
    private static byte[] getSameInitVector() {
        byte[] vector = {
                (byte) 0xA5, (byte) 0xA6, (byte) 0xDB, (byte) 0xA0, (byte) 0x90, (byte) 0x9E, (byte) 0xBA,
                (byte) 0xAA, (byte) 0xF5, (byte) 0xA9, (byte) 0x4, (byte) 0xA4, (byte) 0x73, (byte) 0xEE, (byte) 0xED,
                (byte) 0xF5};
        vector[0] = (byte) (vector[6] & vector[6]);
        vector[1] = (byte) (vector[3] - vector[15]);
        vector[2] = (byte) (vector[2] + vector[0]);
        vector[3] = (byte) (vector[7] + vector[6]);
        vector[4] = (byte) (vector[0] & vector[12]);
        vector[5] = (byte) (vector[3] & vector[2]);
        vector[6] = (byte) (vector[2] - vector[8]);
        vector[7] = (byte) (vector[8] & vector[12]);
        vector[8] = (byte) (vector[10] + vector[7]);
        vector[9] = (byte) (vector[14] & vector[3]);
        vector[10] = (byte) (vector[14] - vector[7]);
        vector[11] = (byte) (vector[14] + vector[15]);
        vector[12] = (byte) (vector[11] | vector[0]);
        vector[13] = (byte) (vector[3] & vector[10]);
        vector[14] = (byte) (vector[9] + vector[10]);
        vector[15] = (byte) (vector[11] - vector[14]);

        return vector;
    }
}

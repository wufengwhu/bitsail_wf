package com.hihonor.datacollector.realtime.crypto.utils;

import com.mchange.lang.IntegerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;


public class HexUtils {


    private static final char[] HEX_CHAR_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static byte[] toBytes(String hexStr) {
        if (hexStr == null || hexStr.isEmpty() || hexStr.length() % 2 != 0) {
            throw new IllegalArgumentException("Illegal Argument in hex to bytes.");
        }
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int n = 0; n < hexStr.length(); n += 2) {
            String item = hexStr.substring(n, n + 2);
            bytes[n / 2] = (byte) IntegerUtils.parseInt(item, 16, 1);
        }
        return bytes;
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexString2ByteArray(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            return new byte[0];
        }
        hexString = hexString.toUpperCase(Locale.ENGLISH);
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String byteArray2HexString(byte[] byteArray) {
        if (null == byteArray || 0 == byteArray.length) {
            return "";
        }
        final StringBuilder hex = new StringBuilder(2 * byteArray.length);
        for (final byte by : byteArray) {
            hex.append(HEX_CHAR_TABLE[(by & 0xF0) >> 4]).append(HEX_CHAR_TABLE[(by & 0x0F)]);
        }
        return hex.toString();
    }
}

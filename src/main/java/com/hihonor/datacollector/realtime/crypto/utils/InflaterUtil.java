package com.hihonor.datacollector.realtime.crypto.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @Description
 * @create 2021-03-22 14:17
 */
public class InflaterUtil {
    private static final byte[] TEMP_BYTE = new byte[2048];

    /**
     * 解压缩
     *
     * @param compressBytes 须要解压缩的字节数组
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] compressBytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Inflater inf = new Inflater();

            byte[] result = new byte[compressBytes.length];

            inf.setInput(compressBytes);
            int byteNum = 0;
            while (!inf.finished()) {
                byteNum = inf.inflate(result);
                out.write(result, 0, byteNum);
            }
            byte[] finalResult = out.toByteArray();
            inf.end();
            return finalResult;
        } catch (DataFormatException e) {
            throw new IOException("occurs an error during decompress data", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}

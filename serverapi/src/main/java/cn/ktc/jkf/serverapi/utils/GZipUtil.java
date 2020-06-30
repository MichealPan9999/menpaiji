package cn.ktc.jkf.serverapi.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cn.ktc.jkf.utils.TextUtils;
import okio.ByteString;

/**
 * 对String进行GZip压缩
 * 对Byte进行GZip解压缩
 *
 * @author hq
 */
public class GZipUtil {
    /**
     * Gzip 压缩数据
     *
     * @param src 需要压缩的数据
     * @return ByteString对象，用于WebSocket
     */
    public static byte[] compress(String src) {
        if (TextUtils.isEmpty(src)) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(src.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return encode;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gzip解压数据
     *
     * @param src 需要解压缩的二进制数据
     * @param offset 从哪里偏移
     * @param length 实际数据长度
     * @return 解压缩后的String对象
     */
    public static String uncompress(byte[] src, int offset, int length) {
        final int BUFFERSIZE = 1024;
        if (src == null || length <= 0) {
            return null;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(src, offset, length);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[BUFFERSIZE];
            int n = 0;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gzip 压缩数据
     *
     * @param src 需要压缩的数据
     * @return ByteString对象，用于WebSocket
     */
    public static ByteString compress2(String src) {
        if (android.text.TextUtils.isEmpty(src)) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(src.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return ByteString.of(encode, 0, encode.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Gzip解压数据
     *
     * @param src 需要解压缩的二进制数据
     * @return 解压缩后的String对象
     */
    public static String uncompress2(ByteString src) {
        final int BUFFERSIZE = 1024;
        if (src == null || src.size() == 0) {
            return null;
        }
        byte[] t = src.toByteArray();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[BUFFERSIZE];
            int n = 0;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

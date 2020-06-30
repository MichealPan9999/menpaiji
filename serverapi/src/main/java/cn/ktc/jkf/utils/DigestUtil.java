package cn.ktc.jkf.utils;

import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 对 MD5/SHA1 等摘要算法的封装
 *
 * @author hq
 */
public class DigestUtil {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	/**
	 * 将一个String执行SHA1摘要算法
	 *
	 * @param src 需要执行的原始字符串
	 * @return 返回byte[]
	 */
	public static byte[] getSha1Bytes(String src) {
		if (src == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			messageDigest.update(src.getBytes(StandardCharsets.UTF_8));
			return messageDigest.digest();
		} catch (Exception e) {
			return src.getBytes(StandardCharsets.UTF_8);
		}
	}

	/**
	 * 将一个String执行SHA1摘要算法
	 *
	 * @param src 需要执行的原始字符串
	 * @return 返回16进制字符串
	 */
	public static String getSha1String(String src) {
		byte[] digest = getSha1Bytes(src);
		return digest == null ? src : getFormattedText(digest);
	}

	/***
	 * 计算文件的SHA1码
	 * 
	 * @param file 文件
	 * @return String 适用于上G大的文件
	 */
	public static String getSha1(File file) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
			FileInputStream in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			messageDigest.update(byteBuffer);
			ch.close();
			in.close();
			return getFormattedText(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 将字符串转换成BASE64编码
	 *
	 * @param src 原始字符串
	 * @return 转换后的字符串
	 */
	public static String toBase64(String src) {
		String result = "";
		try {
			if (src != null) {
				// result = new String(Base64.encode(src.getBytes(StandardCharsets.UTF_8),
				// Base64.NO_WRAP), StandardCharsets.UTF_8);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					result = Base64.getEncoder().encodeToString(src.getBytes());
				}else {
					result = new String(android.util.Base64.encode(src.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP), StandardCharsets.UTF_8);
				}
			}
		} catch (Exception ignored) {
			result = src;
		}
		return result;
	}

	/**
	 * 将BASE64字符串转换成原始字符串
	 *
	 * @param base64 BASE64字符串
	 * @return 原始字符串
	 */
	public static String fromBase64(String base64) {
		String result = "";
		try {
			if (base64 != null) {
				// result = new String(Base64.decode(base64, Base64.NO_WRAP),
				// StandardCharsets.UTF_8);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					result = new String(Base64.getDecoder().decode(base64));
				}else{
					result = new String(android.util.Base64.decode(base64, android.util.Base64.NO_WRAP),StandardCharsets.UTF_8);
				}
			}
		} catch (Exception ignored) {
			result = base64;
		}
		return result;
	}

	/**
	 * 对用户输入的密码执行混淆，然后发送到后台请求 混淆规则: 1. 原始密码+"KTC-JKF"字符串 2. 执行 SHA1 3.
	 * SHA1结果再次+"KTC-JKF"字符串 4. 执行 SHA1
	 *
	 * @param password 原始的密码
	 * @return 混淆后的密码
	 */
	public static String hashPassword(String password) {
		final String SALT = "KTC-JKF";
		if (password == null) {
			return "";
		}
		String after = password;
		after = getSha1String(after + SALT);
		after = getSha1String(after + SALT);
		return after;
	}

	/**
	 * 将byte转换成16进制数据
	 *
	 * @param bytes
	 * @return
	 */
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
}

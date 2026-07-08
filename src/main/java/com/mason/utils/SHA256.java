package com.mason.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    /**
     * 获取SHA256加密后的字符串
     * @param str 原始字符串
     * @return 64 位小写十六进制加密字符串
     */
    public static String getSHA256(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            // 获取 SHA-256 加密实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 加密失败", e);
        }
    }
}

package com.mason.utils;

import java.util.Random;

public class RandomUsernameUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    /**
     * 生成指定长度的随机用户名
     * @param length 用户名长度
     * @return 随机用户名
     */
    public static String generateUsername(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }

        StringBuilder username = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            username.append(CHARACTERS.charAt(index));
        }
        return username.toString();
    }

    /**
     * 生成默认长度(8位)的随机用户名
     * @return 随机用户名
     */
    public static String generateUsername() {
        return generateUsername(8);
    }
}

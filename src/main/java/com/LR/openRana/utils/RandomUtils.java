package com.LR.openRana.utils;

import java.security.SecureRandom;

/**
 * 提供安全随机数生成的工具类。
 */
public class RandomUtils {

    // 定义可用于生成随机十六进制字符串的字符集
    private static final String HEX_CHARS = "0123456789ABCDEF";

    // 定义可用于生成随机数字字符串的字符集
    private static final String NUMBER_CHARS = "0123456789";

    // 定义可用于生成随机 alphanumeric 字符串的字符集
    private static final String ALL_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // 使用 SecureRandom 生成安全随机数
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成一个格式为xxxx-xxxx-xxxx-xxxx-xxxx的随机会话Token。
     *
     * @return 生成的会话Token字符串。
     */
    public static String generateToken() {
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                tokenBuilder.append(HEX_CHARS.charAt(SECURE_RANDOM.nextInt(HEX_CHARS.length())));
            }
            if (i < 4) { // 在每个部分之后除了最后一个添加短横线
                tokenBuilder.append('-');
            }
        }
        return tokenBuilder.toString();
    }

    /**
     * 生成指定长度的随机 alphanumeric 字符串。
     *
     * @param length 需要生成的字符串的长度。
     * @return 生成的随机字符串。
     */
    public static String generateAllCharsRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHARS.charAt(SECURE_RANDOM.nextInt(ALL_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机数字字符串。
     *
     * @param length 需要生成的字符串的长度。
     * @return 生成的随机数字字符串。
     */
    public static String generateNumberRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMBER_CHARS.charAt(SECURE_RANDOM.nextInt(NUMBER_CHARS.length())));
        }
        return sb.toString();
    }
    // 其他可能的随机数生成方法也可以放在这里
}

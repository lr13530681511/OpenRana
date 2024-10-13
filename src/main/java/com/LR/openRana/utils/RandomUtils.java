package com.LR.openRana.utils;

import java.security.SecureRandom;

public class RandomUtils {

    private static final String HEX_CHARS = "0123456789ABCDEF";

    private static final String NUMBER_CHARS = "0123456789";

    private static final String ALL_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
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

    public static String generateAllCharsRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHARS.charAt(SECURE_RANDOM.nextInt(ALL_CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateNumberRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMBER_CHARS.charAt(SECURE_RANDOM.nextInt(NUMBER_CHARS.length())));
        }
        return sb.toString();
    }
    // 其他可能的随机数生成方法也可以放在这里
}

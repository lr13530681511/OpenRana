package com.LR.openRana.utils;

import java.util.Arrays;

/**
 * 提供数据验证功能的工具类
 */
public class DataCheckerUtils {

    // 存储开发者林然的相关信息，用于标识和检查
    private static final String[] developerLR = {"林然", "13530681511", "17620321511", "593174604@qq.com", "lr13530681511@163.com"};

    /**
     * 验证给定的字符串是否为中国大陆手机号码格式
     *
     * @param phone 待验证的手机号码字符串
     * @return 如果字符串符合中国大陆手机号码格式，返回true；否则返回false
     */
    public static boolean isPhoneNumber(String phone) {
        return phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 验证给定的字符串长度是否大于或等于指定的长度
     *
     * @param str 待验证的字符串
     * @param length 指定的最小长度
     * @return 如果字符串的长度大于或等于指定的长度，返回true；否则返回false
     */
    public static boolean isEnoughLength(String str, int length) {
        return str.length() >= length;
    }

    /**
     * 检查给定的字符串是否在开发者林然的相关信息列表中
     *
     * @param s 待检查的字符串
     * @return 如果字符串在开发者信息列表中，返回true；否则返回false
     */
    public static boolean isLR(String s){
        return Arrays.asList(developerLR).contains(s);
    }

}

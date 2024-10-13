package com.LR.openRana.utils;

import java.util.Arrays;

public class DataCheckerUtils {

    private static final String[] developerLR = {"林然", "13530681511", "17620321511", "593174604@qq.com", "lr13530681511@163.com"};

    public static boolean isPhoneNumber(String phone) {
        return phone.matches("^1[3-9]\\d{9}$");
    }

    public static boolean isEnoughLength(String str, int length) {
        return str.length() >= length;
    }

    public static boolean isLR(String s){
        return Arrays.asList(developerLR).contains(s);
    }

}

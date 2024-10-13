package com.LR.openRana.utils;

public class DateInitUtils {

    public static long cacheDayToMillis(long days) {
        return days * 24 * 60 * 60 * 1000;
    }

    public static long cacheMinuteToMillis(long minutes) {
        return minutes * 60 * 1000;
    }

    public static int cacheDayToSecond(int days) {
        return days * 60 * 60 * 24;
    }

    public static int cacheMinuteToSecond(int minutes) {
        return minutes * 60;
    }


}

package com.LR.openRana.utils;

/**
 * 日期转换工具类，提供时间单位之间的转换方法
 */
public class DateInitUtils {

    /**
     * 将天数转换为毫秒数
     *
     * @param days 天数，可以是任意长的整数
     * @return 转换后的毫秒数
     */
    public static long cacheDayToMillis(long days) {
        return days * 24 * 60 * 60 * 1000;
    }

    /**
     * 将分钟数转换为毫秒数
     *
     * @param minutes 分钟数，可以是任意长的整数
     * @return 转换后的毫秒数
     */
    public static long cacheMinuteToMillis(long minutes) {
        return minutes * 60 * 1000;
    }

    /**
     * 将天数转换为秒数
     *
     * @param days 天数，可以是任意的整数
     * @return 转换后的秒数
     */
    public static int cacheDayToSecond(int days) {
        return days * 60 * 60 * 24;
    }

    /**
     * 将分钟数转换为秒数
     *
     * @param minutes 分钟数，可以是任意的整数
     * @return 转换后的秒数
     */
    public static int cacheMinuteToSecond(int minutes) {
        return minutes * 60;
    }

}

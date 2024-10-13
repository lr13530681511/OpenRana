package com.LR.openRana.utils;


import com.LR.openRana.common.LLException;

import java.util.*;
import java.util.stream.Collectors;

public class ListUtils {

    /**
     * 将逗号分隔的字符串转换为Object列表。
     *
     * @param str      以逗号分隔的字符串
     * @param objClazz 目标对象类型
     * @param <T>      泛型类型参数
     * @return 转换后的列表
     */
    public <T> List<T> stringToList(String str, Class<T> objClazz) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(","))
                .map(s -> convertStringToObject(s.trim(), objClazz))
                .collect(Collectors.toList());
    }

    /**
     * 将字符串转换为指定类型的对象。
     *
     * @param value      待转换的字符串
     * @param objectType 目标对象类型
     * @param <T>        泛型类型参数
     * @return 转换后的对象
     * @throws IllegalArgumentException 如果转换失败
     */
    public <T> T convertStringToObject(String value, Class<T> objectType) {
        try {
            if (objectType.equals(Integer.class)) {
                return objectType.cast(Integer.valueOf(value));
            } else if (objectType.equals(Double.class)) {
                return objectType.cast(Double.valueOf(value));
            } else if (objectType.equals(Boolean.class)) {
                return objectType.cast(Boolean.valueOf(value));
            } else {
                return objectType.cast(value); // 默认假设是String类型
            }
        } catch (Exception e) {
            throw new LLException("无法将字符串 '" + value + "' 转换为 " + objectType.getName());
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void swap(List l, int i, int j) {
        Object tmp = l.get(i);
        l.set(i, l.get(j));
        l.set(j, tmp);
    }

    public static <T> List<T> merge(List<T> a, List<T> b) {
        a.removeAll(b);
        a.addAll(b);
        return a;
    }

    public static <T> List<T> add(List<T> a, List<T> b) {
        List<T> ts = new ArrayList<>();
        ts.addAll(a);
        ts.addAll(b);
        return ts;
    }

    /**
     * 将逗号分隔的键值对字符串解析成List<Map<String, String>>。
     *
     * @param keyValuePairsString 逗号分隔的键值对字符串，各条目之间用分号分隔
     * @return 解析后的List<Map < String, String>>对象
     */
    public static List<Map<String, Object>> parseKeyValuesIntoMapList(String keyValuePairsString) {
        List<Map<String, Object>> result = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(keyValuePairsString, ";");
        while (tokenizer.hasMoreTokens()) {
            String keyValuePairs = tokenizer.nextToken();
            Map<String, Object> map = new HashMap<>();
            StringTokenizer pairTokenizer = new StringTokenizer(keyValuePairs, ",");

            while (pairTokenizer.hasMoreTokens()) {
                String keyValue = pairTokenizer.nextToken();
                int separatorIndex = keyValue.indexOf('=');
                if (separatorIndex > 0) {
                    String key = keyValue.substring(0, separatorIndex).trim();
                    String value = keyValue.substring(separatorIndex + 1).trim();
                    map.put(key, value);
                }
            }

            result.add(map);
        }

        return result;
    }


    /**
     * 将以空格分隔的数字字符串解析成List<Integer>。
     *
     * @param numberString 以空格分隔的数字字符串
     * @return 解析后的List<Integer>对象
     * @throws IllegalArgumentException 如果解析失败
     */
    public static List<Integer> spaceSeparatedStringToIntList(String numberString) {
        try {
            String[] numbers = numberString.split("\\s+");
            return Arrays.stream(numbers)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法将字符串 '" + numberString + "' 解析为List<Integer>");
        }
    }


}

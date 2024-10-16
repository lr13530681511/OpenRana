package com.LR.openRana.utils;


import java.util.HashMap;

/**
 * @author Adam agtech-team
 * @email info@ag-tech.cn
 * @date 2020年06月30日 上午10:00:00
 */
public class MapUtils<T, S> extends HashMap<String, Object> {

    @Override
    public MapUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static MapUtils<String, Object> putS(String key, Object value) {
        return new MapUtils<String, Object>().put(key, value);
    }

    public static MapUtils<String, String> putS(String key, String value) {
        return new MapUtils<String, String>().put(key, value);
    }
}

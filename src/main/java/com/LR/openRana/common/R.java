package com.LR.openRana.common;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 * 继承自HashMap，用于统一API返回格式
 * 包含状态码（code）和消息（msg）
 * 可通过静态方法error和ok来创建不同状态的实例
 *
 * @author LR
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 默认构造方法，初始化状态码为0，消息为"success"
     */
    public R() {
        put("code", 0);
        put("msg", "success");
    }

    /**
     * 创建一个表示错误的R实例，使用默认的未知异常状态码和消息
     *
     * @return 错误实例
     */
    public static R error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知异常，请联系管理员");
    }

    /**
     * 创建一个表示错误的R实例，使用给定的消息
     *
     * @param msg 错误消息
     * @return 错误实例
     */
    public static R error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    /**
     * 创建一个表示错误的R实例，使用给定的状态码和消息
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return 错误实例
     */
    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    /**
     * 创建一个表示成功的R实例，可以自定义消息
     *
     * @param msg 成功消息
     * @return 成功实例
     */
    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    /**
     * 创建一个表示成功的R实例，将给定的Map合并到R实例中
     *
     * @param map 要合并的Map
     * @return 成功实例
     */
    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    /**
     * 创建一个表示成功的R实例，默认状态
     *
     * @return 成功实例
     */
    public static R ok() {
        return new R();
    }

    /**
     * 向R实例中添加键值对，并返回实例本身，支持链式调用
     *
     * @param key   键
     * @param value 值
     * @return R实例
     */
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}

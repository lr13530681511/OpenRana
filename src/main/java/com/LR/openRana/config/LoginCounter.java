package com.LR.openRana.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 登录计数器类，用于管理和记录用户的登录错误次数及最后一次访问IP。
 * 支持并发操作，适用于多线程环境。
 */
@Slf4j
@Data
public class LoginCounter {

    // 用户ID到登录计数器的映射，支持并发修改
    private static final Map<Long, LoginCounter> counter = new ConcurrentHashMap<>();
    // 用户手机号到登录计数器的映射，支持并发修改
    private static final Map<String, LoginCounter> phoneCounter = new ConcurrentHashMap<>();
    // 默认错误登录尝试的超时时间，单位未指定，需结合具体业务理解
    private static final long defaultOverTime = 5;
    // 记录最后一次访问的IP地址
    private String lastAccessIP;
    // 登录错误次数，使用原子长整型以支持并发操作
    private AtomicLong errorTimes = new AtomicLong(0L);

    /**
     * 当登录失败时调用，增加错误次数并更新最后访问IP。
     *
     * @param uid          用户ID
     * @param lastAccessIP 最后一次访问的IP地址
     */
    public static void whenError(Long uid, String userName, String lastAccessIP) {
        counter.putIfAbsent(uid, new LoginCounter());
        counter.computeIfPresent(uid, (aLong, loginCounter) -> {
            loginCounter.errorTimes.getAndIncrement(); // 增加错误次数
            loginCounter.lastAccessIP = lastAccessIP; // 更新最后访问IP
            return loginCounter;
        });
        log.info("用户名：{}，登录失败次数：{}", userName, counter.get(uid).errorTimes.get());
    }

    /**
     * 判断错误登录尝试是否超过默认超时时间。
     *
     * @param uid 用户ID
     * @return 如果错误登录次数超过默认超时时间，返回true，否则返回false
     */
    public static boolean overErrorTime(Long uid) {
        return counter.getOrDefault(uid, new LoginCounter()).errorTimes.get() > defaultOverTime;
    }

    /**
     * 当登录成功时调用，重置错误次数并更新最后访问IP。
     *
     * @param uid          用户ID
     * @param lastAccessIP 最后一次访问的IP地址
     */
    public static void whenSuccessfulLogin(Long uid, String lastAccessIP) {
        counter.putIfAbsent(uid, new LoginCounter());
        counter.computeIfPresent(uid, (aLong, loginCounter) -> {
            loginCounter.errorTimes = new AtomicLong(0L); // 重置错误次数
            loginCounter.lastAccessIP = lastAccessIP; // 更新最后访问IP
            return loginCounter;
        });
    }
}


package com.LR.openRana.module.account.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Redis操作仓库类，提供了一些常用的Redis操作方法
 */
@Component
public class RedisRepository {

    // 时间单位：秒
    private final TimeUnit timeUnit = TimeUnit.SECONDS;

    // StringRedisTemplate用于操作Redis中的字符串数据
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造方法，通过依赖注入StringRedisTemplate
     *
     * @param stringRedisTemplate StringRedisTemplate实例
     */
    @Autowired
    public RedisRepository(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置键值对，并可指定过期时间（以秒为单位）
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒），如果为0则表示永不过期
     */
    public void set(String key, String value, long seconds) {
        if (seconds > 0) {
            this.stringRedisTemplate.opsForValue().set(key, value, seconds, this.timeUnit);
        } else {
            this.stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 获取指定键的值
     *
     * @param key 键
     * @return 值，如果键不存在则返回null
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 判断某个键是否存在
     *
     * @param key 键
     * @return 如果键存在则返回true，否则返回false
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 删除指定的键
     *
     * @param key 键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @return 如果过期时间设置成功则返回true，否则返回false
     */
    public boolean expire(String key, long seconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, seconds, this.timeUnit));
    }

    /**
     * 递增指定键的数值型值，指定步长
     *
     * @param key  键
     * @param delta 步长
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减指定键的数值型值，指定步长
     *
     * @param key  键
     * @param delta 步长
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 获取键的剩余过期时间（秒）
     *
     * @param key 键
     * @return 剩余过期时间（秒），如果键不存在或永不过期则返回-1
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, this.timeUnit);
    }

    /**
     * 获取所有键的集合
     *
     * @param template StringRedisTemplate实例
     * @return 包含所有键的Set集合
     */
    public Set<String> getAllKeys(StringRedisTemplate template) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match("*").build(); // 匹配所有key
        Cursor<String> cursor = template.scan(options);
        while (cursor.hasNext()) {
            keys.add(cursor.next());
        }
        cursor.close(); // 关闭游标，释放资源
        return keys;
    }

    /**
     * 解析字符串中的对象字符串，提取大括号内的内容
     *
     * @param input 输入字符串，包含待解析的对象字符串
     * @return 包含解析出的对象字符串的列表
     */
    public List<String> parseObjectString(String input) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\(([^()]*)\\)");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String s = "{" + matcher.group(1) + "}";
            result.add(s);
        }
        return result;
    }
}

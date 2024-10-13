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

@Component
public class RedisRepository {

    private final TimeUnit timeUnit = TimeUnit.SECONDS;

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisRepository(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置键值对，过期时间为秒
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void set(String key, String value, long seconds) {
        if (seconds > 0) {
            this.stringRedisTemplate.opsForValue().set(key, value, seconds, this.timeUnit);
        } else {
            this.stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 获取键对应的值
     *
     * @param key 键
     * @return 值，如果键不存在则返回null
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }


    // 判断某个键是否存在
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    // 删除指定的键
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    // 设置键的过期时间
    public boolean expire(String key, long seconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, seconds, this.timeUnit));
    }

    // 递增指定的键的值，指定步长
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    // 递减指定的键的值，指定步长
    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, this.timeUnit);
    }

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

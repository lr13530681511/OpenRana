package com.LR.openRana.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.DB}")
    private int aiDB;


    /**
     * AI缓存库使用 ： 0号库
     * 创建并配置一个连接到Redis的StringRedisTemplate Bean。
     * 这个方法不接受参数，因为它依赖于类级别的host和port属性来建立连接。
     */

    @Bean
    public StringRedisTemplate connectAccountCache() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(this.host, this.port);
        config.setDatabase(aiDB);
        StringRedisTemplate template = new StringRedisTemplate();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        template.setConnectionFactory(factory); // 使用Lettuce客户端建立连接
        log.info("持久化日志 - Redis: Account-server缓存库已连接");
        return template;
    }


}

package com.LR.openRana.common;

import com.LR.openRana.config.ThreadPoolsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 配置和提供线程池执行器的类
 * 该类继承自ThreadPoolsConfig，用于配置和初始化一个线程池任务执行器
 */
@Deprecated
@Configuration
public class Executor extends ThreadPoolsConfig {

    /**
     * 配置并返回一个线程池任务执行器
     * 根据在配置类中定义的参数，初始化并配置线程池任务执行器
     *
     * @return ThreadPoolTaskExecutor 配置好的线程池任务执行器实例
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(getCorePoolSize());
        executor.setMaxPoolSize(getMaximumPoolSize());
        executor.setQueueCapacity(getQueueCapacity());
        executor.setKeepAliveSeconds(getKeepAliveSeconds());
        executor.setThreadNamePrefix(getThreadPoolName());
        executor.initialize();
        return executor;
    }
}

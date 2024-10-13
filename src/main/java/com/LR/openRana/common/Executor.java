package com.LR.openRana.common;

import com.LR.openRana.config.ThreadPoolsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class Executor extends ThreadPoolsConfig {

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

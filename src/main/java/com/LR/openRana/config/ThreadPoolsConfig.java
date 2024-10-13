package com.LR.openRana.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ThreadPoolsConfig {


    @Value("${thread.core-pool-size}")
    private int corePoolSize;

    @Value("${thread.max-pool-size}")
    private int maximumPoolSize;

    @Value("${thread.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Value("${thread.queue-capacity}")
    private int queueCapacity;

    @Value("${thread.pool-name}")
    private String threadPoolName;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }
}

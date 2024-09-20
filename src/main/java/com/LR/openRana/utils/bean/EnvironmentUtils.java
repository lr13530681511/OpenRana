package com.LR.openRana.utils.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 环境工具类，提供环境检查和获取当前环境的功能
 */
@Component
public class EnvironmentUtils {

    /**
     * 自动注入应用上下文
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 判断当前环境是否为开发环境
     *
     * @return 如果是开发环境，返回true；否则返回false
     */
    public boolean isDevEnvironment() {
        Environment environment = applicationContext.getEnvironment();
        return environment.acceptsProfiles("dev");
    }

    /**
     * 获取当前环境的名称
     *
     * @return 当前环境的名称，如果没有设置环境则返回"unknown"
     */
    public String getEnvironment() {
        Environment environment = applicationContext.getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : "unknown";
    }
}

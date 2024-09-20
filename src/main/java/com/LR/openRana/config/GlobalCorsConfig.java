package com.LR.openRana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局CORS配置类，用于配置所有请求的跨域规则
 */
@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    /**
     * 添加CORS映射，配置请求的跨域规则
     *
     * @param registry CORS注册对象，用于添加CORS规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置跨域规则
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许所有源
                .allowCredentials(true) // 允许携带凭证
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 显式列出允许的方法
                .maxAge(3600); // 设置预检请求有效期
    }
}

package com.LR.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

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
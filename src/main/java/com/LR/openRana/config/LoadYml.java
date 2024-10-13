package com.LR.openRana.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadYml {

    @Value("${spring.application.name}")
    public static String appName;
}

package com.LR.openRana.utils.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtils {

    @Autowired
    private ApplicationContext applicationContext;

    public boolean isDevEnvironment() {
        Environment environment = applicationContext.getEnvironment();
        return environment.acceptsProfiles("dev");
    }

    public String getEnvironment() {
        Environment environment = applicationContext.getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : "unknown";
    }
}


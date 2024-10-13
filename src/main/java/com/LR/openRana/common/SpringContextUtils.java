package com.LR.openRana.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Context 工具类
 *
 * @author LR
 * @email 593174604@qq.com
 * @date 2016年11月29日 下午11:45:51
 */
@Component
@Slf4j
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Class<?> getType(String name) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getType(name);
    }

}

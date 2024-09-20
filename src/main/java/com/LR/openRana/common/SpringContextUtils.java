package com.LR.openRana.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Context 工具类，用于在应用中获取 Spring 上下文中的 Bean 实例
 *
 * @author LR
 * @email 593174604@qq.com
 */
@Component
@Slf4j
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置 Spring 上下文ApplicationContext，由Spring框架自动调用
     *
     * @param applicationContext Spring 上下文ApplicationContext
     * @throws BeansException 如果发生异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    /**
     * 根据 Bean 的名称获取 Bean 实例
     *
     * @param name Bean 的名称
     * @return Bean 实例，如果 SpringContextUtils 未初始化或找不到 Bean，则返回 null
     */
    public static Object getBean(String name) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getBean(name);
    }

    /**
     * 根据 Class 类型获取 Bean 实例
     *
     * @param clazz Bean 的类型
     * @param <T>   Bean 的类型泛型
     * @return Bean 实例，如果 SpringContextUtils 未初始化或找不到 Bean，则返回 null
     */
    public static <T> T getBean(Class<T> clazz) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据 Bean 的名称和类型获取 Bean 实例
     *
     * @param name        Bean 的名称
     * @param requiredType Bean 的类型
     * @param <T>         Bean 的类型泛型
     * @return Bean 实例，如果 SpringContextUtils 未初始化或找不到 Bean，则返回 null
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 检查 Spring 上下文中是否包含指定名称的 Bean
     *
     * @param name Bean 的名称
     * @return 如果包含该 Bean，则返回 true，否则返回 false
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 检查指定名称的 Bean 是否为单例模式
     *
     * @param name Bean 的名称
     * @return 如果该 Bean 是单例模式，则返回 true，否则返回 false
     */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
     * 根据 Bean 的名称获取 Bean 的类型
     *
     * @param name Bean 的名称
     * @return Bean 的类型，如果 SpringContextUtils 未初始化，则返回 null
     */
    public static Class<?> getType(String name) {
        if (null == applicationContext) {
            log.debug("SpringContextUtils not initial");
            return null;
        }
        return applicationContext.getType(name);
    }

}

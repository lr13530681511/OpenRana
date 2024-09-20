package com.LR.openRana.config;

import com.LR.openRana.module.account.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于声明一个方法所需的权限
 * 此注解可用于方法级别，运行时有效，可以被JVM读取
 */
@Target(ElementType.METHOD)  // 定义了注解的使用范围，此处为方法
@Retention(RetentionPolicy.RUNTIME)  // 定义了注解的生命周期，此处为运行时
public @interface PermissionRequired {
    /**
     * 定义所需的角色类型
     * 默认为GUEST，即游客角色
     * @return 角色类型
     */
    RoleType value() default RoleType.GUEST;

    /**
     * 应用名称
     * 默认为空字符串
     * @return 应用名称字符串
     */
    String appName() default "";

    /**
     * 模块名称
     * 默认为空字符串
     * 添加此字段则自动注册接口权限，并且自动记录接口权限信息
     * @return 模块名称字符串
     */
    String moduleName() default "";

    /**
     * 权限名称
     * 默认为空字符串
     * @return 权限名称字符串
     */
    String permissionName() default "";
}

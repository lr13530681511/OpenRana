package com.LR.openRana.config;


import com.LR.openRana.module.account.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionRequired {
    RoleType value() default RoleType.GUEST;

    String appName() default "";

    String moduleName() default "";

    String permissionName() default "";
}

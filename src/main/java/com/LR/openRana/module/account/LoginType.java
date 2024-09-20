/**
 * 登录类型枚举
 * 用于定义用户登录时的认证方式
 */
package com.LR.openRana.module.account;

import com.LR.openRana.common.LLException;

public enum LoginType {
    // 手机号登录
    PHONE("phone"),
    // 邮箱登录
    EMAIL("email"),
    // 用户名登录
    USERNAME("username"),
    // 微信登录
    WECHAT("wechat");
    // 枚举内部类的属性，代表每个登录类型的值
    final String value;

    /**
     * 构造函数
     *
     * @param value 每个登录类型的字符串表示
     */
    LoginType(String value) {
        this.value = value;
    }

    /**
     * 获取登录类型的值
     *
     * @return 登录类型的字符串表示
     */
    public String getValue() {
        return value;
    }

    public static LoginType fromValue(String value) {
        for (LoginType loginType : values()) {
            if (loginType.getValue().equals(value)) {
                return loginType;
            }
        }
        throw new LLException("未知登录方式"); // 如果没有匹配的值，可以返回null，或者抛出异常，根据需求决定
    }

}

package com.LR.openRana.config;

import lombok.Getter;

@Getter
public enum AuthenticationMethod {

    ROLE_TYPE_CHECK(1, "角色类型检查"),
    ROLE_CHECK(2, "角色检查"),
    PERMISSION_CHECK(3, "权限检查");

    // 获取 value 的方法
    private final int value;

    // 获取 description 的方法
    private final String description;

    // 构造方法
    private AuthenticationMethod(int value, String description) {
        this.value = value;
        this.description = description;
    }

}


package com.LR.openRana.config;

import java.util.List;

@Deprecated
public class PassPermissionFilter {

    // 使用 final 关键字确保列表不可更改
    public static final List<String> NON_TOKEN_REQUIRED_URLS = List.of(
            "/account/login",
            "/account/loginByPhone",
            "/account/sign",
            "/account/sendCaptcha",
            "/account/findPasswd"

    );

    // 防止实例化这个配置类
    private PassPermissionFilter() {
        throw new AssertionError("Cannot instantiate configuration class.");
    }

    // 可选: 提供一个获取列表的方法
    public static List<String> getNonTokenRequiredUrls() {
        return NON_TOKEN_REQUIRED_URLS;
    }
}

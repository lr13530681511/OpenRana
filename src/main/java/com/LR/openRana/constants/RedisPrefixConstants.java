package com.LR.openRana.constants;

/**
 * Redis键前缀常量类
 * 本类用于定义和账户相关的Redis操作的键前缀
 * 通过定义常量来统一和简化Redis键的创建，提高代码的可读性和可维护性
 */
public class RedisPrefixConstants {

    // 手机验证码的Redis键前缀
    // 用于区分和存储短信验证码相关数据
    public static final String PHONE_CAPTCHA_PREFIX = "PHONE_CAPTCHA_";

    // 账户令牌的Redis键前缀
    // 用于区分和存储账户认证令牌相关数据
    public static final String ACCOUNT_TOKEN_PREFIX = "ACCOUNT_TOKEN_";

}

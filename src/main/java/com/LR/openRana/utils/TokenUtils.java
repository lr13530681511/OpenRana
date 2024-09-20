package com.LR.openRana.utils;

import com.LR.openRana.module.account.AccountToken;
import com.LR.openRana.module.account.AccountUser;
import com.LR.openRana.module.sso.SSOToken;

/**
 * Token相关工具类，提供单点登录令牌（SSOToken）的生成和解析功能
 */
public class TokenUtils {

    /**
     * 根据应用令牌生成单点登录令牌（SSOToken）
     *
     * @param token   应用令牌，包含账户和用户信息
     * @param appName 应用名称，用于标识SSOToken所属的应用
     * @return 加密后的单点登录令牌字符串
     */
    public static String generateSSOToken(AccountToken token, String appName) {
        // 获取账户关联的用户信息
        AccountUser user = token.getAccount().getUser();
        // 构建SSOToken对象，包含应用名称、账户UID、令牌、过期时间、用户UID、昵称、头像、最大角色、权限等信息
        SSOToken ssoToken = new SSOToken.Builder()
                .appName(appName)
                .accountUid(token.getAccountUid())
                .token(token.getToken())
                .expiredTime(token.getExpiredTime())
                .userUId(user.getUid())
                .nickName(user.getName())
                .avatar(user.getAvatar())
                .maxRole(token.getAccount().getMaxRole())
                .permissions(token.getAccount().getPermissions())
                .build();
        // 将SSOToken对象转换为JSON字符串
        String json = JSONUtils.toJSONString(ssoToken);
        // 加密JSON字符串并返回
        return DataFactoryUtils.encrypt(json);
    }

    /**
     * 解析单点登录令牌字符串为SSOToken对象
     *
     * @param token 加密后的单点登录令牌字符串
     * @return 解析后的SSOToken对象
     */
    public static SSOToken parseSSOToken(String token) {
        // 解密令牌字符串
        String json = DataFactoryUtils.decrypt(token);
        // 将JSON字符串转换为SSOToken对象并返回
        return JSONUtils.toJavaObject(json, SSOToken.class);
    }

    /**
     * 获取单点登录令牌中包含的账户UID
     *
     * @param ssoTokenJsonStr 加密后的单点登录令牌字符串
     * @return 令牌中的账户UID
     */
    public static Long getAccountUid(String ssoTokenJsonStr) {
        // 解析令牌并返回账户UID
        return parseSSOToken(ssoTokenJsonStr).getAccountUid();
    }

    /**
     * 获取单点登录令牌中包含的用户UID
     *
     * @param ssoTokenJsonStr 加密后的单点登录令牌字符串
     * @return 令牌中的用户UID
     */
    public static Long getUserUid(String ssoTokenJsonStr) {
        // 解析令牌并返回用户UID
        return parseSSOToken(ssoTokenJsonStr).getUserUId();
    }

    /**
     * 获取单点登录令牌中的令牌字符串
     *
     * @param ssoTokenJsonStr 加密后的单点登录令牌字符串
     * @return 令牌中的令牌字符串
     */
    public static String getToken(String ssoTokenJsonStr) {
        // 解析令牌并返回令牌字符串
        return parseSSOToken(ssoTokenJsonStr).getToken();
    }
}

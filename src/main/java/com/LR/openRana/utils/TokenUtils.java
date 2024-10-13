package com.LR.openRana.utils;

import com.LR.openRana.module.account.AccountToken;
import com.LR.openRana.module.account.AccountUser;
import com.LR.openRana.module.sso.SSOToken;

public class TokenUtils {

    public static String generateSSOToken(AccountToken token, String appName) {
        AccountUser user = token.getAccount().getUser();
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
        String json = JSONUtils.toJSONString(ssoToken);
        return DataFactoryUtils.encrypt(json);
    }

    public static SSOToken parseSSOToken(String token) {
        String json = DataFactoryUtils.decrypt(token);
        return JSONUtils.toJavaObject(json, SSOToken.class);
    }

    public static Long getAccountUid(String ssoTokenJsonStr) {
        return parseSSOToken(ssoTokenJsonStr).getAccountUid();
    }

    public static Long getUserUid(String ssoTokenJsonStr) {
        return parseSSOToken(ssoTokenJsonStr).getUserUId();
    }

    public static String getToken(String ssoTokenJsonStr) {
        return parseSSOToken(ssoTokenJsonStr).getToken();
    }
}

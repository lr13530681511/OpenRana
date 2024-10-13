package com.LR.openRana.module.sso;

import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.RoleType;
import lombok.Data;

import java.util.Set;

// 单点登录令牌实体类，用于存储用户的登录信息
@Data
public class SSOToken {

    // 应用名称
    private String appName;

    // 账户唯一标识符
    private Long accountUid;

    // 用户唯一标识符
    private Long userUId;

    // 用户昵称
    private String nickName;

    // 用户头像URL
    private String avatar;

    // 访问令牌
    private String token;

    // 过期时间（毫秒）
    private long expiredTime;

    // 最高权限类型
    private RoleType maxRole;

    private Set<AccountPermission> permissions;

    // 构建器模式，用于创建SSOToken对象
    public static class Builder {
        private String appName;
        private Long accountUid;
        private Long userUId;
        private String nickName;
        private String avatar;
        private String token;
        private long expiredTime;
        private RoleType maxRole;
        private Set<AccountPermission> permissions;

        public Builder() {}

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder accountUid(Long accountUid) {
            this.accountUid = accountUid;
            return this;
        }

        public Builder userUId(Long userUId) {
            this.userUId = userUId;
            return this;
        }

        public Builder nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder expiredTime(long expiredTime) {
            this.expiredTime = expiredTime;
            return this;
        }

        public Builder maxRole(RoleType maxRole) {
            this.maxRole = maxRole;
            return this;
        }

        public Builder permissions(Set<AccountPermission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public SSOToken build() {
            SSOToken ssoToken = new SSOToken();
            ssoToken.setAppName(appName);
            ssoToken.setAccountUid(accountUid);
            ssoToken.setUserUId(userUId);
            ssoToken.setNickName(nickName);
            ssoToken.setAvatar(avatar);
            ssoToken.setToken(token);
            ssoToken.setExpiredTime(expiredTime);
            ssoToken.setMaxRole(maxRole);
            ssoToken.setPermissions(permissions);
            return ssoToken;
        }
    }
}

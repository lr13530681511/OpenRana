package com.LR.openRana.module.sso;

import com.LR.openRana.config.AuthenticationMethod;
import com.LR.openRana.module.account.RoleType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class CheckRoleResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 账户的唯一标识符，由数据库自动生成

    private String userName;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String token;

    private String appName;

    private String url;

    private RoleType roleType;

    private Boolean result;

    private String message;

    private String path;

    private LocalDateTime time;

    private AuthenticationMethod method;

    // 默认构造器，通常用于框架内部使用
    public CheckRoleResult() {
    }

    // 内部静态类作为Builder
    public static class Builder {
        private Long uid;
        private String userName;
        private String token;
        private String appName;
        private String url;
        private RoleType roleType;
        private Boolean result;
        private String message;
        private String path;
        private LocalDateTime time;
        private AuthenticationMethod method;

        // 构造函数可以公开或私有，这里选择公开
        public Builder() {
        }

        // 提供链式调用的setter方法
        public Builder withUid(Long uid) {
            this.uid = uid;
            return this;
        }

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withRoleType(RoleType roleType) {
            this.roleType = roleType;
            return this;
        }

        public Builder withResult(Boolean result) {
            this.result = result;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withTime(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public Builder withMethod(AuthenticationMethod method) {
            this.method = method;
            return this;
        }

        // 构建并返回CheckRoleResult对象
        public CheckRoleResult build() {
            CheckRoleResult checkRoleResult = new CheckRoleResult();
            checkRoleResult.uid = this.uid;
            checkRoleResult.userName = this.userName;
            checkRoleResult.token = this.token;
            checkRoleResult.appName = this.appName;
            checkRoleResult.url = this.url;
            checkRoleResult.roleType = this.roleType;
            checkRoleResult.result = this.result;
            checkRoleResult.message = this.message;
            checkRoleResult.path = this.path;
            checkRoleResult.time = this.time;
            checkRoleResult.method = this.method;
            return checkRoleResult;
        }
    }
}

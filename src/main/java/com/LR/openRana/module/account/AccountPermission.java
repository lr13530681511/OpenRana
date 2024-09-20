package com.LR.openRana.module.account;

import com.LR.openRana.module.account.repository.AccountPermissionRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "account_permission")
@NoArgsConstructor
@AllArgsConstructor
public class AccountPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 账户的唯一标识符，由数据库自动生成

    private String permissionName;

    private String appName;

    private String modelName;

    private String controller;

    private String function;

    private String path;

    private String url;

    public AccountPermission register(AccountPermissionRepository repository) {
        return repository.save(this);
    }

    private AccountPermission(Builder builder) {
        this.uid = builder.uid;
        this.permissionName = builder.permissionName;
        this.appName = builder.appName;
        this.modelName = builder.modelName;
        this.controller = builder.controller;
        this.function = builder.function;
        this.path = builder.path;
        this.url = builder.url;
    }

    // Builder class
    public static class Builder {
        private Long uid;
        private String permissionName;
        private String appName;
        private String modelName;
        private String controller;
        private String function;
        private String path;
        private String url;

        // Field setters directly
        public Builder uid(Long uid) {
            this.uid = uid;
            return this;
        }

        public Builder permissionName(String permissionName) {
            this.permissionName = permissionName;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder controller(String controller) {
            this.controller = controller;
            return this;
        }

        public Builder function(String function) {
            this.function = function;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public AccountPermission build() {
            return new AccountPermission(this);
        }
    }

}

package com.LR.openRana.module.sso;


import com.LR.openRana.module.account.AccountPermission;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table
public class PermissionRegistrationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 账户的唯一标识符，由数据库自动生成

    private String appName;

    private LocalDateTime registrationTime;

    private Boolean result;

    private String msg;

    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<AccountPermission> accountPermissions;

    public static Builder builder() {
        return new Builder();
    }

    // Inner builder class
    public static class Builder {
        public Long uid;
        public String appName;
        public LocalDateTime registrationTime;
        public Boolean result;
        public String msg;
        public Set<AccountPermission> accountPermissions = new HashSet<>();

        public Builder withUid(Long uid) {
            this.uid = uid;
            return this;
        }

        public Builder withAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder withRegistrationTime(LocalDateTime registrationTime) {
            this.registrationTime = registrationTime;
            return this;
        }

        public Builder withResult(Boolean result) {
            this.result = result;
            return this;
        }

        public Builder withMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder withAccountPermissions(Set<AccountPermission> accountPermissions) {
            this.accountPermissions.addAll(accountPermissions);
            return this;
        }

        public PermissionRegistrationResult build() {
            PermissionRegistrationResult permissionRegistrationResult = new PermissionRegistrationResult();
            permissionRegistrationResult.uid = this.uid;
            permissionRegistrationResult.appName = this.appName;
            permissionRegistrationResult.registrationTime = this.registrationTime;
            permissionRegistrationResult.result = this.result;
            permissionRegistrationResult.msg = this.msg;
            permissionRegistrationResult.accountPermissions = this.accountPermissions;
            return permissionRegistrationResult;
        }
    }

}

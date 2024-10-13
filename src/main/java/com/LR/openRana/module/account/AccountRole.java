package com.LR.openRana.module.account;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "account_role")
public class AccountRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 账户的唯一标识符，由数据库自动生成

    private String roleName;

    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<AccountPermission> permissions;

    private RoleType roleType;

    public void authorize(Set<AccountPermission> accountPermissions) {
        this.permissions = accountPermissions;
    }

    public void unAuthorize(Set<AccountPermission> accountPermissions) {
        this.permissions.removeAll(accountPermissions);
    }

    public void authorize(AccountPermission accountPermission) {
        this.permissions.add(accountPermission);
    }

    public void unAuthorize(AccountPermission accountPermission) {
        this.permissions.remove(accountPermission);
    }
}

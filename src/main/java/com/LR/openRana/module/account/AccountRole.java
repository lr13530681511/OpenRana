package com.LR.openRana.module.account;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "account_role")
public class AccountRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 账户的唯一标识符，由数据库自动生成

    private String roleName;

    @ManyToMany(cascade = CascadeType.DETACH)
    private List<AccountPermission> permissions;

    private RoleType roleType;

    public void authorize(List<AccountPermission> accountPermissions) {
        this.permissions = accountPermissions;
    }

    public void authorize(AccountPermission accountPermission) {
        this.permissions.add(accountPermission);
    }
}

package com.LR.openRana.module.account.vo;

import lombok.Data;

import java.util.Set;

@Data
public class AuthorizeVO {

    private Long roleId;

    private Long permissionId;

    private Set<Long> permissionIds;
}

package com.LR.openRana.module.account.service;

import com.LR.openRana.common.LLException;
import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.AccountRole;
import com.LR.openRana.module.account.repository.AccountPermissionRepository;
import com.LR.openRana.module.account.repository.AccountRepository;
import com.LR.openRana.module.account.repository.AccountRoleRepository;
import com.LR.openRana.utils.JpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionService {

    private AccountRepository accountRepository;

    private AccountRoleRepository roleRepository;

    private AccountPermissionRepository permissionRepository;

    @Autowired
    public PermissionService(AccountRepository accountRepository, AccountRoleRepository roleRepository, AccountPermissionRepository permissionRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // 查询所有权限
    public List<AccountPermission> findAll(AccountPermission permission) {
        return permissionRepository.findAll(JpaUtils.buildSpecificationForFuzzySearch(permission));
    }

    // 批量授权
    public AccountRole authorize(Long roleId, Set<Long> permissionIds) {
        AccountRole role = roleRepository.findById(roleId).orElseThrow(() -> new LLException("未找到角色"));
        Set<AccountPermission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.authorize(permissions);
        return roleRepository.save(role);
    }

    // 批量取消授权
    public AccountRole unAuthorize(Long roleId, Set<Long> permissionIds) {
        AccountRole role = roleRepository.findById(roleId).orElseThrow(() -> new LLException("未找到角色"));
        Set<AccountPermission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.unAuthorize(permissions);
        return roleRepository.save(role);
    }

    // 角色授权
    public AccountRole authorize(Long roleId, Long permissionId) {
        AccountPermission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new LLException("权限未找到"));
        AccountRole role = roleRepository.findById(roleId).orElseThrow(() -> new LLException("角色未找到"));
        role.authorize(permission);
        return roleRepository.save(role);
    }

    // 取消授权
    public AccountRole unAuthorize(Long roleId, Long permissionId) {
        AccountPermission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new LLException("权限未找到"));
        AccountRole role = roleRepository.findById(roleId).orElseThrow(() -> new LLException("角色未找到"));
        role.unAuthorize(permission);
        return roleRepository.save(role);
    }


}

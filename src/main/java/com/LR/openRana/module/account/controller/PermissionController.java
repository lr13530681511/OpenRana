package com.LR.openRana.module.account.controller;

import com.LR.openRana.common.R;
import com.LR.openRana.config.PermissionRequired;
import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.AccountRole;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.repository.AccountRoleRepository;
import com.LR.openRana.module.account.service.PermissionService;
import com.LR.openRana.module.account.vo.AuthorizeVO;
import com.LR.openRana.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping(value = "/permission")
public class PermissionController {

    private PermissionService service;

    private AccountRoleRepository repository;

    @Autowired
    public PermissionController(PermissionService service, AccountRoleRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping(value = "/list")
    @PermissionRequired(value = RoleType.ADMIN, permissionName = "查询权限", appName = "用户账户", moduleName = "permission")
    public List<AccountPermission> findAll(@RequestBody AccountPermission permission) {
        return service.findAll(permission);
    }

    @PermissionRequired(value = RoleType.ADMIN, permissionName = "授权", appName = "用户账户", moduleName = "permission")
    @RequestMapping(value = "/authorize")
    public R authorize(@RequestBody AuthorizeVO vo) {
        try {

            return R.ok(MapUtils.putS("result",
                    service.authorize(vo.getRoleId(), vo.getPermissionIds())));
        } catch (Exception e) {
            return R.error("授权失败");
        }


    }

    @PermissionRequired(value = RoleType.ADMIN, permissionName = "取消授权", appName = "用户账户", moduleName = "permission")
    @RequestMapping(value = "/unAuthorize")
    public R unAuthorize(@RequestBody AuthorizeVO vo) {
        try {
            return R.ok(MapUtils.putS("result",
                    service.unAuthorize(vo.getRoleId(), vo.getPermissionIds())));
        } catch (Exception e) {
            return R.error("取消授权失败");
        }

    }


    @PermissionRequired(value = RoleType.ADMIN, permissionName = "查询角色", appName = "用户账户", moduleName = "permission")
    @GetMapping(value = "/listForRole")
    public R listForRole() {
        return R.ok(MapUtils.putS("result",repository.findAll()));

    }

    @PermissionRequired(value = RoleType.ADMIN, permissionName = "添加角色", appName = "用户账户", moduleName = "permission")
    @RequestMapping(value = "/addRole")
    public AccountRole addRole(@RequestBody AccountRole role) {
        role.setPermissions(new HashSet<>());
        return repository.save(role);
    }

    @PermissionRequired(value = RoleType.ADMIN, permissionName = "删除角色", appName = "用户账户", moduleName = "permission")
    @RequestMapping(value = "/deleteRole")
    public R deleteRole(@RequestBody Long id) {
        repository.deleteById(id);
        return R.ok("删除成功");
    }
}

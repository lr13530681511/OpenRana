package com.LR.openRana.module.sso.service;

import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.repository.AccountPermissionRepository;
import com.LR.openRana.module.sso.PermissionRegistrationResult;
import com.LR.openRana.module.sso.repository.PermissionRegistrationResultRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * 权限注册服务类
 */
@Slf4j
@Service
public class PermissionRegistrationMachine {

    private AccountPermissionRepository repository;

    private PermissionRegistrationResultRepository resultRepository;

    /**
     * 构造函数，用于注入权限仓库和注册结果仓库
     *
     * @param repository    权限仓库
     * @param resultRepository 注册结果仓库
     */
    @Autowired
    public PermissionRegistrationMachine(AccountPermissionRepository repository, PermissionRegistrationResultRepository resultRepository) {
        this.repository = repository;
        this.resultRepository = resultRepository;
    }

    /**
     * 注册权限，并记录结果
     *
     * @param requests 权限注册请求对象
     * @return 返回权限注册结果对象
     */
    public PermissionRegistrationResult register(PermissionRegistrationResult requests) {
        requests.setResult(savePermission(requests.getAccountPermissions()));
        if (!requests.getResult()) {
            requests.setMsg("权限注册失败");
        } else {
            requests.setMsg("权限注册成功");
        }
        requests.setRegistrationTime(LocalDateTime.now());
        return resultRepository.save(requests);
    }

    /**
     * 保存或更新权限信息
     *
     * @param accountPermissions 权限集合
     * @return 返回操作是否成功
     */
    @Transactional
    protected Boolean savePermission(Set<AccountPermission> accountPermissions) {
        try {
            for (AccountPermission accountPermission : accountPermissions) {
                Optional<AccountPermission> dbPermission = repository.findByPath(accountPermission.getPath());
                dbPermission.ifPresent(permission -> accountPermission.setUid(permission.getUid()));
                repository.saveAndFlush(accountPermission);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打印权限注册日志
     *
     * @param result 权限注册结果对象
     */
    private void printLog(PermissionRegistrationResult result) {
        log.info(
                "应用信息:\n" +
                        "-----------------------------------\n" +
                        "应用名称: {}\n" +
                        "权限注册时间: {}\n" +
                        "权限注册结果: {}\n" +
                        "-----------------------------------",
                result.getAppName(), result.getRegistrationTime(), result.getMsg()
        );
    }
}

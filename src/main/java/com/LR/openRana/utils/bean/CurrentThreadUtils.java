package com.LR.openRana.utils.bean;

import com.LR.openRana.common.LLException;
import com.LR.openRana.common.SpringContextUtils;
import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.repository.AccountTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CurrentThreadUtils {

    // 取当前访问用户的token
    public String getCurrentToken(HttpServletRequest request) {
        return request.getHeader("Token");
    }


    // 获取用户权限路径
    public Set<String> getCurrentUserPermissionPaths(String token) {
        com.LR.openRana.module.account.Account account = getCurrentAccount(token);
        return account.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(AccountPermission::getPath)
                .collect(Collectors.toSet());
    }

    // 获取当前用户权限等级及以下等级列表
    public Set<RoleType> getCurrentUserRoles(String token) {
        return RoleType.getRolesBelowOrEqual(getCurrentMaxRoleType(token));
    }

    // 取当前访问用户的角色
    public Set<RoleType> getCurrentRoleType(String tokenString) {
        AccountTokenRepository repository = getRepository();
        if (!repository.existsByToken(tokenString)) {
            return Collections.singleton(RoleType.GUEST);
        }
        return repository.findByToken(tokenString).get().getAccount().getRoleTypes();
    }

    // 取当前访问用户的最大角色
    public RoleType getCurrentMaxRoleType(String tokenString) {
        AccountTokenRepository repository = getRepository();
        if (!repository.existsByToken(tokenString)) {
            return RoleType.GUEST;
        }
        return repository.findByToken(tokenString).get().getAccount().getMaxRole();
    }

    // 通过request取当前访问用户的角色
    public Set<RoleType> getCurrentRoleType(HttpServletRequest request) {
        if (request.getHeader("Token") == null) {
            return Collections.singleton(RoleType.GUEST);
        }
        return getCurrentAccount(request).getRoleTypes();
    }

    // 通过request取当前访问用户
    public Account getCurrentAccount(HttpServletRequest request) {
        return getCurrentAccount(request.getHeader("Token"));
    }

    // 通过token取当前访问用户
    public Account getCurrentAccount(String tokenString) {
        AccountTokenRepository repository = getRepository();
        if (!repository.existsByToken(tokenString)) {
            throw new LLException("当前访问角色为访客");
        }
        return repository.findByToken(tokenString).get().getAccount();
    }

    // 取AccountRepository
    private AccountTokenRepository getRepository() {
        return SpringContextUtils.getBean(AccountTokenRepository.class);
    }

}

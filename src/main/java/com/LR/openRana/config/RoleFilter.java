package com.LR.openRana.config;

import com.LR.openRana.common.LLException;
import com.LR.openRana.common.SpringContextUtils;
import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.repository.AccountTokenRepository;
import com.LR.openRana.module.sso.CheckRoleResult;
import com.LR.openRana.module.sso.repository.CheckRoleResultRepository;
import com.LR.openRana.utils.bean.CurrentThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色过滤器类，用于验证用户的角色和权限
 */
@Slf4j
@Component
public class RoleFilter {

    /**
     * 本地调用权限验证
     *
     * @param r 角色检查结果对象
     */
    public final void checkPermissions(CheckRoleResult r) {
        r.setTime(LocalDateTime.now());
        if (r.getRoleType().equals(RoleType.GUEST)) {
            checkRoleType(r, true, "无需权限");
            if (r.getUrl().contains("permissionRegistration")) {
                r.setUserName("权限注册专用接口");
                r.setToken("未知");
                r.setAppName("请按时间查询注册记录");
            }
            return;
        }
        if (r.getToken() == null || !SpringContextUtils.getBean(AccountTokenRepository.class).existsByToken(r.getToken())) {
            checkRoleType(r, false, "token失效/未传入Token");
            throw new LLException("token失效，请传入有效Token");
        }
        r.setUserName(getUserName(r.getToken()));
        if (enoughPermission(r)) {
            checkRoleType(r, true, "权限通过");
        } else {
            checkRoleType(r, false, "权限不足");
            throw new LLException("当前用户权限不足");
        }
    }

    /**
     * 远程调用权限验证
     *
     * @param requests 角色检查结果对象
     * @return 验证后的角色检查结果对象
     */
    public final CheckRoleResult remoteCheckPermissions(CheckRoleResult requests) {
        requests.setTime(LocalDateTime.now());
        if (requests.getRoleType().equals(RoleType.GUEST)) {
            checkRoleType(requests, true, "无需权限");
            return requests;
        }
        if (requests.getToken().isBlank() || !SpringContextUtils.getBean(AccountTokenRepository.class).existsByToken(requests.getToken())) {
            checkRoleType(requests, false, "Token失效/未传入Token");
        } else {
            requests.setUserName(getUserName(requests.getToken()));
            if (enoughPermission(requests)) {
                checkRoleType(requests, true, "权限通过");
            } else {
                checkRoleType(requests, false, "权限不足");
            }
        }
        return requests;
    }

    /**
     * 验证权限明细
     *
     * @param requests 角色检查结果对象
     * @return 是否有足够权限
     */
    private boolean checkPermissionPath(CheckRoleResult requests) {
        // TODO 待实现 权限明细验证
        return true;
    }

    /**
     * 验证并记录角色类型权限结果
     *
     * @param requests 角色检查结果对象
     * @param result   验证结果
     * @param msg      结果消息
     */
    private void checkRoleType(CheckRoleResult requests, Boolean result, String msg) {
        requests.setResult(result);
        requests.setMessage(msg);
        printLog(requests);
        Objects.requireNonNull(SpringContextUtils.getBean(CheckRoleResultRepository.class)).save(requests);
    }

    /**
     * 验证用户是否具有足够的权限等级
     *
     * @param r 角色检查结果对象
     * @return 是否有足够权限等级
     */
    private boolean enoughPermission(CheckRoleResult r) {
        Set<RoleType> userRoles = getUserRoles(r.getToken());
        return userRoles.contains(r.getRoleType());
    }

    /**
     * 验证用户是否具有足够的权限明细
     *
     * @param r 角色检查结果对象
     * @return 是否有足够权限明细
     */
    private boolean enoughPermissionPath(CheckRoleResult r) {
        String path = r.getPath();
        Set<String> paths = getUserPermissionPaths(r.getToken());
        return paths.contains(path);
    }

    /**
     * 获取用户的角色集合
     *
     * @param token 用户令牌
     * @return 用户角色集合
     */
    private Set<RoleType> getUserRoles(String token) {
        return RoleType.getRolesBelowOrEqual(new CurrentThreadUtils().getCurrentMaxRoleType(token));
    }

    /**
     * 获取用户的权限路径集合
     *
     * @param token 用户令牌
     * @return 用户权限路径集合
     */
    private Set<String> getUserPermissionPaths(String token) {
        Account account = new CurrentThreadUtils().getCurrentAccount(token);
        return account.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(AccountPermission::getPath)
                .collect(Collectors.toSet());
    }

    /**
     * 打印日志
     *
     * @param r 角色检查结果对象
     */
    private void printLog(CheckRoleResult r) {
        log.info(
                "应用信息:\n" +
                        "-----------------------------------\n" +
                        "应用名称: {}\n" +
                        "用户名称: {}\n" +
                        "请求接口: {}\n" +
                        "请求时间: {}\n" +
                        "请求结果: {}\n" +
                        "-----------------------------------",
                r.getAppName(), r.getUserName(), r.getUrl(), r.getTime(), r.getMessage()
        );
    }

    /**
     * 获取用户名称
     *
     * @param token 用户令牌
     * @return 用户名称
     */
    private String getUserName(String token) {
        if (token.isEmpty() || token.equals("null") || !SpringContextUtils.getBean(AccountTokenRepository.class).existsByToken(token)) {
            return "未知";
        }
        return SpringContextUtils.getBean(AccountTokenRepository.class).findByToken(token).get().getAccount().getUserName();
    }
}

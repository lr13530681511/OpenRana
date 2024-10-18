package com.LR.openRana.config;

import com.LR.openRana.common.SpringContextUtils;
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


@Slf4j
@Component
public class RoleFilter {

    // 本地調用权限验证
    public CheckRoleResult checkPermissions(CheckRoleResult r) {
        if (r.getRoleType() != RoleType.GUEST){
            checkTokenAndIgnore(r);
        }
        if (r.getResult() == null ) {
            switch (r.getMethod()) {
                case ROLE_TYPE_CHECK:
                    RoleTypeCheck(r);
                    break;
                case ROLE_CHECK:
                    RoleCheck(r);
                    break;
                case PERMISSION_CHECK:
                    break;
            }
        }
        return r;
    }

    //    ==============================================角色权限校验======================================================
    private void RoleCheck(CheckRoleResult r) {
        if (enoughPermissionPath(r)) {
            record(r, true, "权限通过");
        } else {
            record(r, false, "权限不足");
        }
    }

    // 验证权限路径
    private boolean enoughPermissionPath(CheckRoleResult r) {
        String path = r.getPath();
        Set<String> paths = new CurrentThreadUtils().getCurrentUserPermissionPaths(r.getToken());
        return paths.contains(path);
    }


    //    ==============================================系统权限校验======================================================
    private void RoleTypeCheck(CheckRoleResult r) {
        // 检查用户角色是否包含注解指定的角色
        r.setUserName(getUserName(r.getToken()));
        if (enoughRoleTpe(r)) {
            record(r, true, "权限通过");
        } else {
            record(r, false, "权限不足");
        }
    }

    // 验证权限等级
    private boolean enoughRoleTpe(CheckRoleResult r) {
        Set<RoleType> userRoles = new CurrentThreadUtils().getCurrentUserRoles(r.getToken());
        return userRoles.contains(r.getRoleType());
    }


    //    ==============================================公共方法======================================================
    // 校验权限之前都要检查token/游客模式
    private void checkTokenAndIgnore(CheckRoleResult r) {
        // 排除权限注册接口
        if (r.getUrl().contains("permissionRegistration")) {
            r.setUserName("权限注册专用接口");
            r.setToken("未知");
            r.setAppName("请按时间查询注册记录");
            record(r, true, "无需权限");
            return;
        }
        if (r.getRoleType().equals(RoleType.GUEST)) {
            record(r, true, "无需权限");
            return;
        }
        if (r.getToken() == null || !Objects.requireNonNull(SpringContextUtils.getBean(AccountTokenRepository.class)).existsByToken(r.getToken())) {
            record(r, false, "token失效/未传入Token");
        }
    }

    // 记录权限校验结果
    private void record(CheckRoleResult r, Boolean result, String msg) {
        r.setTime(LocalDateTime.now());
        r.setResult(result);
        r.setMessage(msg);
        printLog(r);
        Objects.requireNonNull(SpringContextUtils.getBean(CheckRoleResultRepository.class)).save(r);
    }

    // 日志输出
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

    // 取当用户名
    private String getUserName(String token) {
        AccountTokenRepository repository = SpringContextUtils.getBean(AccountTokenRepository.class);
        if (token.isEmpty() || token.equals("null") || !repository.existsByToken(token)) {
            return "未知";
        }
        return repository.findByToken(token).get().getAccount().getUserName();
    }
}




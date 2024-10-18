package com.LR.openRana.aop;


import com.LR.openRana.common.LLException;
import com.LR.openRana.config.AuthenticationMethod;
import com.LR.openRana.config.PermissionRequired;
import com.LR.openRana.config.RoleFilter;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.sso.CheckRoleResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {

    // 选本系统使用的鉴权方式
    private final AuthenticationMethod authenticationMethod = AuthenticationMethod.ROLE_TYPE_CHECK;

    @Around("@annotation(permission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PermissionRequired permission) throws Throwable {
        checkUserRoleFromSession(permission.value(), joinPoint);
        // 如果用户有足够权限，继续执行原方法
        return joinPoint.proceed();
    }

    // 假设这是一个获取当前登录用户角色的方法
    private void checkUserRoleFromSession(RoleType roleType, ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        CheckRoleResult requests = new CheckRoleResult.Builder()
                .withAppName("account-server")
                .withUrl(request.getRequestURL().toString())
                .withRoleType(roleType)
                .withToken(request.getHeader("Token"))
                .withPath(getPath(methodSignature.getMethod()))
                .withMethod(authenticationMethod)
                .build();
        new RoleFilter().checkPermissions(requests);
        if (!requests.getResult()) {
            if ("token失效/未传入Token".equals(requests.getMessage())){
                redirectToSSOLogin(request, response);
            }
//             这行让本地鉴权不起效了
            throw new LLException(requests.getMessage());
        }
    }

    private String getPath(Method method) {
        PermissionRequired annotation = method.getAnnotation(PermissionRequired.class);
        return annotation.moduleName() + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName();
    }

    private void redirectToSSOLogin(HttpServletRequest request, HttpServletResponse response) {
        String url = "https://sso.linergou.ink/account/SSO/ssoLogin?appName=$appName&redirectUrl=$redirectUrl"
                .replace("$appName", "account-server")
                .replace("$redirectUrl", request.getRequestURL().toString());
        // 设置响应状态码为 302（临时重定向）
        response.setStatus(HttpServletResponse.SC_FOUND);
        // 设置 Location 头为重定向 URL
        response.setHeader("Location", url);
        // 结束响应
        try {
            response.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


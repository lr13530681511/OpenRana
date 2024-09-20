package com.LR.openRana.aop;

import com.LR.openRana.config.PermissionRequired;
import com.LR.openRana.config.RoleFilter;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.sso.CheckRoleResult;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 权限方面的处理类，用于拦截需要特定权限的操作
 */
@Aspect
@Component
public class PermissionAspect {

    /**
     * 检查用户是否具有执行注解方法所需的权限
     *
     * @param joinPoint  切入点，用于获取当前执行的方法信息
     * @param permission 方法上声明的PermissionRequired注解
     * @return 如果用户有足够权限，返回原方法执行结果
     * @throws Throwable 如果用户没有足够权限，可能抛出异常
     */
    @Around("@annotation(permission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PermissionRequired permission) throws Throwable {
        // 根据会话检查用户角色和权限
        checkUserRoleFromSession(permission.value(), joinPoint);
        // 如果用户有足够权限，继续执行原方法
        return joinPoint.proceed();
    }

    /**
     * 从会话中检查当前登录用户的角色和权限
     *
     * @param roleType  所需的角色类型
     * @param joinPoint 切入点，用于获取当前执行的方法信息
     */
    private void checkUserRoleFromSession(RoleType roleType, ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 构建角色检查请求
        CheckRoleResult requests = new CheckRoleResult.Builder()
                .withAppName("account-server")
                .withUrl(request.getRequestURL().toString())
                .withRoleType(roleType)
                .withToken(request.getHeader("Token"))
                .withPath(getPath(methodSignature.getMethod()))
                .build();
        // 执行权限检查
        new RoleFilter().checkPermissions(requests);
    }

    /**
     * 获取方法的路径标识，用于权限检查
     *
     * @param method 需要获取路径标识的方法
     * @return 方法的路径标识
     */
    private String getPath(Method method) {
        PermissionRequired annotation = method.getAnnotation(PermissionRequired.class);
        // 构造方法的唯一标识符
        return annotation.moduleName() + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName();
    }
}

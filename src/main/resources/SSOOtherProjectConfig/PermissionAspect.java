package com.LR.ai.aop;

import com.LR.account.common.LLException;
import com.LR.account.config.PermissionRequired;
import com.LR.account.module.account.AccountPermission;
import com.LR.account.module.account.RoleType;
import com.LR.account.module.sso.SSOToken;
import com.LR.account.utils.JSONUtils;
import com.LR.account.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class PermissionAspect {


    @Value("${sso.hostPort}")
    private String hostPort;

    @Value("${sso.checkUrl}")
    private String checkUrl;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${sso.ssoLoginUrl}")
    private String ssoLoginUrl;

    // 远程鉴权
//    @Around("@annotation(permission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PermissionRequired permission) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestUrl = request.getRequestURL().toString();
        String fullUrl = hostPort + checkUrl
                + "?roleType=" + permission.value()
                + "&token=" + request.getHeader("Token")
                + "&appName=" + appName
                + "&url=" + requestUrl;
        ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
        // appName      请求者应用名称
        // userName     请求用户名称
        // url          请求API接口
        // result       验证结果
        // msg          验证备注
        // time         请求时间
        Map<String, Object> result = JSONUtils.toJSONMap(response.getBody());
        if (!result.containsKey("result")) {
            throw new LLException("权限验证失败，请检查权限验证服务端");
        }
        if (!Boolean.parseBoolean(result.get("result").toString())) {
            throw new LLException(result.get("msg").toString());
        }
        // 如果用户有足够权限，继续执行原方法
        return joinPoint.proceed();
    }

    // 本地鉴权
    @Around("@annotation(permission)")
    public Object localCheckPermission(ProceedingJoinPoint joinPoint, PermissionRequired permission) throws Throwable {
        if (permission.value().equals(RoleType.GUEST)) {
            return joinPoint.proceed();
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse(); // 获取 HttpServletResponse
        String token = request.getHeader("Token");
        if (token == null || token.isEmpty()) {
            redirectToSSOLogin(request, response);
            return null; // 避免继续执行后续逻辑
        }
        SSOToken ssoToken = TokenUtils.parseSSOToken(token);
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String permissionPath = getPermissionPath(method);
        RoleType maxRole = ssoToken.getMaxRole();
        List<String> permissions = ssoToken.getPermissions().stream().map(AccountPermission::getPath).toList();
        if (maxRole.getLevel() < permission.value().getLevel()) {
            throw new LLException("用户等级-权限不足");
        }
        if (!"".equals(permission.moduleName())) {
            if (!permissions.contains(permissionPath)) {
                throw new LLException("接口权限-权限不足");
            }
        }
        // 继续执行目标方法并传递 SSOToken 参数
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++){
            if (args[i] instanceof SSOToken) {
                args[i] = ssoToken;
            }
        }
        return joinPoint.proceed(args);

    }

    private String getPermissionPath(Method method) {
        PermissionRequired annotation = method.getAnnotation(PermissionRequired.class);
        return annotation.moduleName() + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName();
    }


    private void redirectToSSOLogin(HttpServletRequest request, HttpServletResponse response) {
        String url = ssoLoginUrl
                .replace("$appName", appName)
                .replace("$redirectUrl", request.getRequestURL().toString());
        // 设置响应状态码为 302（临时重定向）
        response.setStatus(HttpServletResponse.SC_FOUND);
        // 设置 Location 头为重定向 URL
        response.setHeader("Location", hostPort + url);
        // 结束响应
        try {
            response.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

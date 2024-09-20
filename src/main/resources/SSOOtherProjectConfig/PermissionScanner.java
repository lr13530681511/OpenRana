package com.LR.ai.config;

import com.LR.account.config.PermissionRequired;
import com.LR.account.module.account.AccountPermission;
import com.LR.account.module.sso.PermissionRegistrationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


// 权限注册器
@Component
public class PermissionScanner implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${sso.hostPort}")
    private String hostPort;

    @Value("${sso.registrationUrl}")
    private String registrationUrl;

    private final static String packageName = "COM.LR.AI";
    private static final Logger log = LoggerFactory.getLogger(PermissionScanner.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        PermissionRegistrationResult requests = new PermissionRegistrationResult();
        requests.setAppName(appName);
        log.info("------------------------------权限注册地址------------------------------");
        log.info(hostPort + registrationUrl);
        requests.setAccountPermissions(scanForPermissions(getClasses(event.getApplicationContext())));
        sendPostRequest(requests);
    }

    public void sendPostRequest(PermissionRegistrationResult request) {
        String fullUrl = hostPort + registrationUrl;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<PermissionRegistrationResult> response = restTemplate.postForEntity(fullUrl, request, PermissionRegistrationResult.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody().getResult()) {
                log.info("------------------------------权限注册成功------------------------------");
            } else {
                log.error("------------------------------权限注册失败------------------------------");
            }
        }catch (Exception e){
            log.error("------------------------------鉴权系统链接失败，权限注册失败------------------------------");
        }

    }

    private static Set<Class<?>> getClasses(ApplicationContext context) {
        HashSet<Class<?>> classes = new HashSet<>();
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        // Add your base package here
        provider.addIncludeFilter(new AssignableTypeFilter(Object.class));
        Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(packageName);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); // Handle the exception as needed
            }
        }
        return classes;
    }

    // 扫描所有类并收集带有PermissionRequired注解的方法
    public static Set<AccountPermission> scanForPermissions(Set<Class<?>> classes) {
        Set<AccountPermission> permissions = new HashSet<>();
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PermissionRequired.class) && !method.getAnnotation(PermissionRequired.class).appName().isEmpty()) {
                    permissions.add(toAccountPermission(method));
                }
            }
        }
        return permissions;
    }

    private static AccountPermission toAccountPermission(Method method) {
        PermissionRequired annotation = method.getAnnotation(PermissionRequired.class);
        AccountPermission permission = new AccountPermission.Builder()
                .permissionName(annotation.permissionName())
                .appName(annotation.appName())
                .modelName(annotation.moduleName())
                .controller(method.getDeclaringClass().getSimpleName())
                .function(method.getName())
                .path(annotation.moduleName() + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName())
                .url("")
                .build();
        log.info("更新权限：" + permission.getPermissionName() + "-----" + permission.getPath());
        return permission;
    }

    @Override
    public boolean supportsAsyncExecution() {
        return false;
    }
}

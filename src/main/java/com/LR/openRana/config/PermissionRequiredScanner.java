package com.LR.openRana.config;

import com.LR.openRana.common.SpringContextUtils;
import com.LR.openRana.module.account.AccountPermission;
import com.LR.openRana.module.account.repository.AccountPermissionRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

@Component
public class PermissionRequiredScanner implements BeanPostProcessor {


    private static final Logger log = LoggerFactory.getLogger(PermissionRequiredScanner.class);
    private final AccountPermissionRepository repository;

    public PermissionRequiredScanner(AccountPermissionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NotNull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        AccountPermissionRepository repository = SpringContextUtils.getBean(AccountPermissionRepository.class);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PermissionRequired.class)) {
                PermissionRequired annotation = method.getAnnotation(PermissionRequired.class);
                if (!annotation.moduleName().isEmpty()) {
                    AccountPermission permission = new AccountPermission.Builder()
                            .permissionName(annotation.permissionName())
                            .appName(annotation.appName())
                            .modelName(annotation.moduleName())
                            .controller(method.getDeclaringClass().getSimpleName())
                            .function(method.getName())
                            .path(annotation.moduleName() + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName())
                            .url("")
                            .build();
                    assert repository != null;
                    Optional<AccountPermission> oP = repository.findByPath(permission.getPath());
                    if (oP.isPresent()) {
                        permission.setUid(oP.get().getUid());
                        log.info("更新权限：" + permission.getPath());
                    }
                    repository.saveAndFlush(permission);
                }
            }
        }
        return bean;
    }
}

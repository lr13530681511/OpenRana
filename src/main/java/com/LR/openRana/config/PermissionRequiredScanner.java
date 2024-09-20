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

/**
 * 权限要求扫描器，作为BeanPostProcessor的一个实现，
 * 用于在Spring上下文初始化期间扫描带有@PermissionRequired注解的方法，并记录相应的权限信息。
 */
@Component
public class PermissionRequiredScanner implements BeanPostProcessor {

    // 日志记录器，用于记录权限的更新信息
    private static final Logger log = LoggerFactory.getLogger(PermissionRequiredScanner.class);
    private final AccountPermissionRepository repository; // 权限仓库，用于操作权限数据

    // 构造方法，注入权限仓库
    public PermissionRequiredScanner(AccountPermissionRepository repository) {
        this.repository = repository;
    }

    /**
     * 在Bean初始化之前处理BeanPostProcessor方法，
     * 扫描Bean中的所有方法，寻找带有@PermissionRequired注解的方法，并记录权限信息。
     *
     * @param bean      当前正在创建的Bean实例
     * @param beanName  Bean的名称
     * @return          返回处理后的Bean实例
     * @throws BeansException  如果处理过程中发生异常则抛出
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, @NotNull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        AccountPermissionRepository repository = SpringContextUtils.getBean(AccountPermissionRepository.class);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PermissionRequired.class)) {
                PermissionRequired annotation = method.getAnnotation(PermissionRequired.class);
                if (!annotation.appName().isEmpty()) {
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

package com.LR.openRana.task;

import com.LR.openRana.module.account.repository.AccountPermissionRepository;
import com.LR.openRana.utils.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 一个抽象类，用于在应用上下文初始化时执行一些特定的逻辑。
 * 实现这个类的子类需要提供具体的启动时执行逻辑。
 */
public abstract class InitialTask implements ApplicationListener<ContextRefreshedEvent> {

    protected final Logger log = LoggerFactory.getLogger(getClass()); // 日志记录器

    /**
     * 在应用启动时被调用的抽象方法，用于执行启动时的初始化逻辑。
     *
     * @param applicationContext 应用上下文，提供应用配置和bean的访问。
     */
    protected abstract void onStartup(ApplicationContext applicationContext);

    protected AccountPermissionRepository accountPermissionRepository; // 账户权限仓库

    /**
     * 当应用上下文被刷新时监听事件的实现方法。
     * 如果事件关联的应用上下文有父上下文，则不执行任何操作。
     * 否则，初始化 accountPermissionRepository 和 ObjectMapper，
     * 并调用 onStartup 方法。
     *
     * @param event 应用上下文刷新事件。
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 如果当前上下文有父上下文，则不处理
        if (event.getApplicationContext().getParent() != null) {
            return;
        } else {
            // 初始化账户权限仓库和JSON工具
            this.accountPermissionRepository = event.getApplicationContext()
                    .getBean(AccountPermissionRepository.class);
            JSONUtils.setObjectMapper(event.getApplicationContext().getBean(ObjectMapper.class));
            // 调用子类的启动时执行逻辑
            onStartup(event.getApplicationContext());
        }
    }

}


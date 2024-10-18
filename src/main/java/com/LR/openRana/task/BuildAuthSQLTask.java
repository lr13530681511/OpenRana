package com.LR.openRana.task;

import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.AccountRole;
import com.LR.openRana.module.account.AccountUser;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.repository.AccountRepository;
import com.LR.openRana.module.account.repository.AccountRoleRepository;
import com.LR.openRana.module.account.repository.AccountUserRepository;
import com.LR.openRana.utils.DataFactoryUtils;
import com.LR.openRana.utils.RandomUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * 用于构建授权SQL任务的类，主要负责在系统初始化时建立admin账号、角色及其绑定关系。
 */
@Component
public class BuildAuthSQLTask extends InitialTask {

    private ApplicationContext applicationContext;

    /**
     * 在系统启动时执行的任务。
     * 主要包括：
     * 1. 检查并创建admin账号；
     * 2. 检查并创建admin角色；
     * 3. 将admin账号与admin角色绑定。
     *
     * @param applicationContext 应用上下文，用于获取Spring Bean。
     */
    @Override
    protected void onStartup(ApplicationContext applicationContext) {
        log.info("BuildAuthSQLTask start");
        this.applicationContext = applicationContext;
        // 注册admin账号和角色，如果它们不存在的话
        initAccountRole();
        initAccountUser();
        initAdminAccount();
        log.info("BuildAuthSQLTask end");
    }

    private void initAdminAccount() {
        AccountRepository accountRepository = applicationContext.getBean(AccountRepository.class);
        if (!accountRepository.existsByUserName("admin")) {
            // 获取平台事务管理器并开启事务
            PlatformTransactionManager platformTransactionManager = applicationContext
                    .getBean(PlatformTransactionManager.class);
            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus status = platformTransactionManager.getTransaction(transactionDefinition);

            AccountRoleRepository roleRepository = applicationContext.getBean(AccountRoleRepository.class);
            AccountUserRepository userRepository = applicationContext.getBean(AccountUserRepository.class);

            AccountUser adminUser = userRepository.findByName("admin").get();
            Account account = new Account.AccountBuilder()
                    .phoneNumber("17620321511")
                    .email("593174604@qq.com")
                    .userName("admin").build();
            account.setCreateTime(LocalDateTime.now());
            account.setIsActive(true);
            account.setIsNoExpired(true);
            account.setRefreshToken("");
            account.setSalt("000");
            account.setPassWord(DataFactoryUtils.addSalt("admin", account.getSalt()));
            account.setWechatAccessToken("");
            account.setAccountToken(RandomUtils.generateToken());
            account.setWechatOpenId("");
            account.setRoles(new HashSet<>());
            account.bindRole(roleRepository.findByRoleName("admin"));
            account.setUser(adminUser);
            account = accountRepository.save(account);
            adminUser.setAccountUid(account.getUid());
            userRepository.saveAndFlush(adminUser);
            platformTransactionManager.commit(status);
        }
    }

    private void initAccountRole() {
        PlatformTransactionManager platformTransactionManager = applicationContext
                .getBean(PlatformTransactionManager.class);
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus status = platformTransactionManager.getTransaction(transactionDefinition);

        AccountRoleRepository roleRepository = applicationContext.getBean(AccountRoleRepository.class);
        for (RoleType roleType : RoleType.getAll()) {
            if (!roleRepository.existsByRoleName(roleType.getName())) {
                AccountRole role = new AccountRole();
                role.setRoleType(roleType);
                role.setRoleName(roleType.getName());
                role.setPermissions(new HashSet<>());
                roleRepository.save(role);
            }
        }
        platformTransactionManager.commit(status);
    }

    private void initAccountUser() {
        AccountUserRepository userRepository = applicationContext.getBean(AccountUserRepository.class);
        if (!userRepository.existsByName("admin")) {
            PlatformTransactionManager platformTransactionManager = applicationContext
                    .getBean(PlatformTransactionManager.class);
            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus status = platformTransactionManager.getTransaction(transactionDefinition);
            userRepository.save(new AccountUser.AccountUserBuilder()
                    .name("admin")
                    .birth(LocalDate.now())
                    .accountUid(1L).build());
            platformTransactionManager.commit(status);
        }
    }

}

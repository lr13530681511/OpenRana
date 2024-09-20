package com.LR.openRana.module.sso.service;

import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.AccountToken;
import com.LR.openRana.module.account.AccountUser;
import com.LR.openRana.module.account.repository.AccountRepository;
import com.LR.openRana.module.account.repository.AccountTokenRepository;
import com.LR.openRana.module.account.repository.AccountUserRepository;
import com.LR.openRana.module.account.service.CaptchaCacheService;
import com.LR.openRana.module.sso.SSOLoginRequests;
import com.LR.openRana.module.sso.repository.SSOLoginRequestsRepository;
import com.LR.openRana.utils.DataFactoryUtils;
import com.LR.openRana.utils.DateInitUtils;
import com.LR.openRana.utils.RandomUtils;
import com.LR.openRana.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

/**
 * 单点登录服务类，处理登录相关逻辑
 */
@Service
public class SSOService {

    private static final Logger log = LoggerFactory.getLogger(SSOService.class);
    private final SSOLoginRequestsRepository repository;
    private final AccountRepository accountRepository;
    private final AccountTokenRepository tokenRepository;
    private final CaptchaCacheService captchaCacheService;
    private final AccountUserRepository accountUserRepository;

    /**
     * 构造方法，初始化登录请求仓库和账户相关的仓库
     *
     * @param repository            登录请求仓库
     * @param accountRepository     账户仓库
     * @param tokenRepository       账户令牌仓库
     * @param captchaCacheService   验证码缓存服务
     * @param accountUserRepository 账户用户仓库
     */
    @Autowired
    public SSOService(SSOLoginRequestsRepository repository, AccountRepository accountRepository, AccountTokenRepository tokenRepository, CaptchaCacheService captchaCacheService, AccountUserRepository accountUserRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
        this.captchaCacheService = captchaCacheService;
        this.accountUserRepository = accountUserRepository;
    }

    /**
     * 处理登录请求，根据登录类型（手机号或用户名）进行不同的登录验证
     *
     * @param requests 登录请求对象
     */
    public void login(SSOLoginRequests requests) {
        switch (requests.getLoginType()) {
            case PHONE -> {
                Optional<Account> account = accountRepository.findByPhoneNumber(requests.getLoginK());
                checkCaptchaAndLogin(account, requests);
            }
            case USERNAME -> {
                Optional<Account> account = accountRepository.findByUserName(requests.getLoginK());
                checkPasswdAndLogin(account, requests);
            }
            default -> {
                loginFail(requests, "暂不支持的登录类型，目前只支持手机号登录");
            }
        }
    }

    /**
     * 验证密码并登录
     *
     * @param accountO 账户可选对象
     * @param requests 登录请求对象
     */
    private void checkPasswdAndLogin(Optional<Account> accountO, SSOLoginRequests requests) {
        if (accountO.isEmpty()) {
            loginFail(requests, "用户不存在");
        } else {
            Account account = accountO.get();
            if (!account.checkPassword(requests.getLoginV())) {
                loginFail(requests, "密码错误");
            } else {
                loginSuccess(requests, account);
            }
        }
    }

    /**
     * 验证验证码并登录
     *
     * @param accountO 账户可选对象
     * @param requests 登录请求对象
     */
    private void checkCaptchaAndLogin(Optional<Account> accountO, SSOLoginRequests requests) {
        if (captchaCacheService.validateCaptcha(requests.getLoginK(), requests.getLoginV())) {
            if (accountO.isEmpty()) {
                Account account = signUp(requests);
                loginSuccess(requests, account);
                requests.setMsg("注册成功，默认密码为验证码，请及时更改！");
            } else {
                Account account = accountO.get();
                loginSuccess(requests, account);
            }
        } else {
            loginFail(requests, "验证码错误");
        }
    }

    /**
     * 注册新用户
     *
     * @param requests 登录请求对象
     * @return 新注册的账户对象
     */
    private Account signUp(SSOLoginRequests requests) {
        String salt = RandomUtils.generateAllCharsRandomString(8);
        Account account = new Account.AccountBuilder()
                .userName(requests.getLoginK())
                .phoneNumber(requests.getLoginK())
                .salt(salt)
                .passWord(DataFactoryUtils.addSalt(requests.getLoginV(), salt))
                .build();
        account.setEmail("");
        account.setIsNoExpired(true);
        account.setIsActive(true);
        account.setCreateTime(LocalDateTime.now());
        account.setRoles(new HashSet<>());
        accountRepository.save(account);
        AccountUser user = new AccountUser.AccountUserBuilder()
                .name(requests.getLoginK())
                .accountUid(account.getUid())
                .build();
        accountUserRepository.save(user);
        account.setUser(user);
        accountRepository.saveAndFlush(account);
        return account;
    }

    /**
     * 登录成功处理
     *
     * @param requests 登录请求对象
     * @param account  登录账户对象
     */
    private void loginSuccess(SSOLoginRequests requests, Account account) {
        AccountToken accountToken = new AccountToken.AccountTokenBuilder()
                .accountUid(account.getUid())
                .token(RandomUtils.generateToken())
                .expiredTime(DateInitUtils.cacheDayToMillis(7))
                .build();
        tokenRepository.save(accountToken);
        requests.setIsSuccess(true);
        requests.setMsg("登录成功");
        requests.setToken(TokenUtils.generateSSOToken(accountToken, requests.getAppName()));
        account.setAccountToken(accountToken.getToken());
        accountRepository.saveAndFlush(account);
        repository.save(requests);
    }

    /**
     * 登录失败处理
     *
     * @param requests 登录请求对象
     * @param msg      失败原因消息
     */
    private void loginFail(SSOLoginRequests requests, String msg) {
        requests.setIsSuccess(false);
        requests.setToken(null);
        requests.setMsg(msg);
        repository.save(requests);
    }
}

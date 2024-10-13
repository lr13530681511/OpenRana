package com.LR.openRana.module.account.service;

import com.LR.openRana.common.LLException;
import com.LR.openRana.config.LoginCounter;
import com.LR.openRana.module.account.*;
import com.LR.openRana.module.account.repository.AccountRepository;
import com.LR.openRana.module.account.repository.AccountRoleRepository;
import com.LR.openRana.module.account.repository.AccountTokenRepository;
import com.LR.openRana.module.account.repository.AccountUserRepository;
import com.LR.openRana.utils.DataFactoryUtils;
import com.LR.openRana.utils.DateInitUtils;
import com.LR.openRana.utils.RandomUtils;
import com.LR.openRana.utils.bean.SMSUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;


@Transactional
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountUserRepository userRepository;
    private final AccountRoleRepository roleRepository;
    private final AccountTokenRepository tokenRepository;
    private final CaptchaCacheService captchaService;
    private final SMSUtils smsService;

    @Autowired
    public AccountService(AccountRepository accountRepository, AccountUserRepository userRepository, AccountRoleRepository roleRepository, AccountTokenRepository tokenRepository, CaptchaCacheService captchaService, SMSUtils smsService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.captchaService = captchaService;
        this.smsService = smsService;
    }

    public String login(String loginKey, String loginValue, LoginType type, String accessIp) {
        switch (type) {
            case USERNAME -> {
                return flashTokenByPassword(findAccount(loginKey, type), loginValue, accessIp).getToken();
            }
            case PHONE -> {
                return flashTokenByCaptcha(findAccount(loginKey, type), loginValue, accessIp).getToken();
            }
        }
        return "不支持的登录方式";
    }

    public String loginByPhone(String phone, String captcha, String accessIp) {
        checkCaptcha(phone, captcha);
        return flashTokenByCaptcha(findAccount(phone, LoginType.PHONE), captcha, accessIp).getToken();
    }

    public String logout(HttpServletRequest request) {
        tokenRepository.deleteByToken(request.getHeader("Token"));
        return "退出成功";
    }

    public String sign(String phone, String passWord, String captcha, String userName) {
        if (!captchaService.validateCaptcha(phone, captcha)) {
            throw new LLException("验证码错误");
        } else if (accountRepository.existsByPhoneNumber(phone)) {
            throw new LLException("该手机号已注册，一个手机号只可绑定一个用户");
        }
        String salt = RandomUtils.generateAllCharsRandomString(8);
        Account account = new Account.AccountBuilder()
                .userName(userName)
                .phoneNumber(phone)
                .passWord(DataFactoryUtils.addSalt(passWord, salt))
                .salt(salt)
                .build();
        account.setEmail("");
        account.setIsNoExpired(true);
        account.setIsActive(true);
        account.setCreateTime(LocalDateTime.now());
        account.setRoles(new HashSet<>());
        account = accountRepository.saveAndFlush(account);
        AccountUser user = userRepository.saveAndFlush(new AccountUser.AccountUserBuilder()
                .name(phone)
                .accountUid(account.getUid())
                .build());
        account.setUser(user);
        account.bindRole(roleRepository.findByRoleName("user"));

        return accountRepository.saveAndFlush(account).getAccountToken();
    }


    public String changePassword(Account account, String oldPassWord, String newPassWord, String captcha) {

        if (account.checkPassword(oldPassWord) && captchaService.validateCaptcha(account.getPhoneNumber(), captcha)) {
            account.updatePassword(newPassWord);
            return accountRepository.saveAndFlush(account).getAccountToken();
        } else {
            throw new LLException("旧密码验证错误");
        }
    }

    public String changePhone(Account account, String newPhone, String captcha) {
        checkCaptcha(account.getPhoneNumber(), captcha);
        account.setPhoneNumber(newPhone);
        return accountRepository.saveAndFlush(account).getAccountToken();
    }

    public String findPassword(String phone, String captcha, String newPassWord) {
        checkCaptcha(phone, captcha);
        Account account = accountRepository.findByPhoneNumber(phone).orElseThrow(() -> new LLException("该手机号不存在"));
        account.updatePassword(newPassWord);
        return accountRepository.saveAndFlush(account).getAccountToken();
    }

    public String updateAccount(Account account, AccountUser user) {
        userRepository.saveAndFlush(user);
        return accountRepository.saveAndFlush(account).getAccountToken();
    }


    public void sendCaptcha(String phone) {
        if (captchaService.isExist(phone)) {
            throw new LLException("验证码已发送，请稍后再试");
        }
        Captcha captcha = captchaService.insertCaptchaForPhone(phone);
        if (!smsService.sendSMS(captcha.getPhone(), captcha.getCode())) {
            throw new LLException("验证码发送失败");
        }
    }

    public String bindRole(Account account, Long roleId) {
        try {
            AccountRole role = roleRepository.findById(roleId).orElseThrow(() -> new LLException("角色不存在"));
            account.bindRole(role);
            accountRepository.saveAndFlush(account);
            return "角色绑定成功";
        }catch (Exception e){
            return "角色绑定失败";
        }
    }

    public String unBindRole(Account account, Long roleId) {
        try {
            AccountRole role = roleRepository.findById(roleId).orElseThrow(() -> new LLException("角色不存在"));
            account.unBindRole(role);
            accountRepository.saveAndFlush(account);
            return "角色解绑成功";
        }catch (Exception e){
            return "角色解绑失败";
        }
    }

    private AccountToken flashTokenByPassword(Account account, String passWord, String accessIp) {
        if (account.checkPassword(passWord)) {
            AccountToken accountToken = new AccountToken.AccountTokenBuilder()
                    .accountUid(account.getUid())
                    .token(RandomUtils.generateToken())
                    .expiredTime(DateInitUtils.cacheDayToMillis(7))
                    .build();
            accountToken = tokenRepository.saveAndFlush(accountToken);
            account.setAccountToken(accountToken.getToken());
            accountRepository.saveAndFlush(account);
            LoginCounter.whenSuccessfulLogin(account.getUid(), accessIp);
            return accountToken;
        } else {
            LoginCounter.whenError(account.getUid(), account.getUser().getName(), accessIp);
            throw new LLException("密码校验错误");
        }
    }

    private AccountToken flashTokenByCaptcha(Account account, String captcha, String accessIp) {
        checkCaptcha(account.getPhoneNumber(), captcha);
        if (account.isActive()) {
            AccountToken accountToken = new AccountToken.AccountTokenBuilder()
                    .accountUid(account.getUid())
                    .token(RandomUtils.generateToken())
                    .expiredTime(DateInitUtils.cacheDayToMillis(7))
                    .build();
            accountToken = tokenRepository.saveAndFlush(accountToken);
            account.setAccountToken(accountToken.getToken());
            accountRepository.saveAndFlush(account);
            LoginCounter.whenSuccessfulLogin(account.getUid(), accessIp);
            return accountToken;
        }
        throw new LLException("该用户已被禁用");
    }

    /**
     * 验证用户登录凭证。
     *
     * @param loginKey 登录使用的键（根据登录类型不同，可以是用户名、手机号、邮箱或微信OpenID）
     * @param type     登录类型
     * @return 验证通过返回Account对象
     * @throws LLException 如果验证失败抛出异常
     */
    private Account findAccount(String loginKey, LoginType type) {
        return switch (type) {
            case USERNAME ->
                    accountRepository.findByUserName(loginKey).orElseThrow(() -> new LLException("该用户名不存在：" + loginKey + "请前往注册"));
            case PHONE ->
                    accountRepository.findByPhoneNumber(loginKey).orElseThrow(() -> new LLException("该手机号不存在：" + loginKey + "请前往注册"));
            case WECHAT ->
                    accountRepository.findByWechatOpenId(loginKey).orElseThrow(() -> new LLException("该微信号不存在：" + loginKey + "请前往注册"));
            case EMAIL ->
                    accountRepository.findByEmail(loginKey).orElseThrow(() -> new LLException("该邮箱不存在：" + loginKey + "请前往注册"));
            default -> throw new LLException("当前只支持手机号登录或用户名登录，请选择支持方式登录！");
        };
    }

    private void checkCaptcha(String phone, String captcha) {
        if (!captchaService.validateCaptcha(phone, captcha)) {
            throw new LLException("验证码错误");
        }
    }


}

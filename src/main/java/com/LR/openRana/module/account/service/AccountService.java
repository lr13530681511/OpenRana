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
    /**
     * 根据不同的登录方式执行相应的登录操作
     *
     * @param loginKey 登录标识，可以是用户名或电话号码
     * @param loginValue 登录值，可以是密码或验证码
     * @param type 登录方式，支持用户名登录和手机验证码登录
     * @param accessIp 客户端的IP地址
     * @return 登录成功后返回令牌字符串，不支持的登录方式返回错误信息
     */
    public String login(String loginKey, String loginValue, LoginType type, String accessIp) {
        switch (type) {
            case USERNAME -> {
                // 通过用户名和密码进行登录
                return flashTokenByPassword(findAccount(loginKey, type), loginValue, accessIp).getToken();
            }
            case PHONE -> {
                // 通过手机验证码进行登录
                return flashTokenByCaptcha(findAccount(loginKey, type), loginValue, accessIp).getToken();
            }
        }
        return "不支持的登录方式";
    }

    /**
     * 通过手机验证码进行登录
     *
     * @param phone 用户的电话号码
     * @param captcha 用户输入的验证码
     * @param accessIp 客户端的IP地址
     * @return 登录成功后返回令牌字符串
     */
    public String loginByPhone(String phone, String captcha, String accessIp) {
        // 验证验证码的正确性
        checkCaptcha(phone, captcha);
        // 通过验证码生成并返回令牌
        return flashTokenByCaptcha(findAccount(phone, LoginType.PHONE), captcha, accessIp).getToken();
    }

    /**
     * 用户退出登录
     *
     * @param request HTTP请求，用于获取请求头中的令牌
     * @return 返回退出登录的结果信息
     */
    public String logout(HttpServletRequest request) {
        // 从请求头中获取令牌，并删除相应的会话信息
        tokenRepository.deleteByToken(request.getHeader("Token"));
        return "退出成功";
    }
    /**
     * 用户注册方法
     * 通过手机号、密码、验证码和用户名完成用户注册
     *
     * @param phone 手机号码
     * @param passWord 密码
     * @param captcha 验证码
     * @param userName 用户名
     * @return 注册后生成的账户令牌
     */
    public String sign(String phone, String passWord, String captcha, String userName) {
        // 验证验证码是否正确
        if (!captchaService.validateCaptcha(phone, captcha)) {
            throw new LLException("验证码错误");
        }
        // 检查手机号是否已注册
        else if (accountRepository.existsByPhoneNumber(phone)) {
            throw new LLException("该手机号已注册，一个手机号只可绑定一个用户");
        }
        // 生成随机盐值
        String salt = RandomUtils.generateAllCharsRandomString(8);
        // 创建账户对象并设置属性
        Account account = new Account.AccountBuilder()
                .userName(userName)
                .phoneNumber(phone)
                .passWord(DataFactoryUtils.addSalt(passWord, salt))
                .salt(salt)
                .build();
        // 初始化账户的其他属性
        account.setEmail("");
        account.setIsNoExpired(true);
        account.setIsActive(true);
        account.setCreateTime(LocalDateTime.now());
        account.setRoles(new HashSet<>());
        // 保存并刷新账户信息
        account = accountRepository.saveAndFlush(account);
        // 创建并保存用户信息，关联账户
        AccountUser user = userRepository.saveAndFlush(new AccountUser.AccountUserBuilder()
                .name(phone)
                .accountUid(account.getUid())
                .build());
        account.setUser(user);
        // 绑定用户角色
        account.bindRole(roleRepository.findByRoleName("user"));

        // 保存并返回账户令牌
        return accountRepository.saveAndFlush(account).getAccountToken();
    }

    /**
     * 用户修改密码方法
     * 验证旧密码和验证码后，修改密码
     *
     * @param account 用户账户
     * @param oldPassWord 旧密码
     * @param newPassWord 新密码
     * @param captcha 验证码
     * @return 修改密码后生成或更新的账户令牌
     */
    public String changePassword(Account account, String oldPassWord, String newPassWord, String captcha) {
        // 验证旧密码和验证码
        if (account.checkPassword(oldPassWord) && captchaService.validateCaptcha(account.getPhoneNumber(), captcha)) {
            // 更新密码并保存账户信息
            account.updatePassword(newPassWord);
            return accountRepository.saveAndFlush(account).getAccountToken();
        } else {
            // 如果验证失败，抛出异常
            throw new LLException("旧密码验证错误");
        }
    }

    /**
     * 用户修改手机号方法
     * 验证验证码后，修改手机号
     *
     * @param account 用户账户
     * @param newPhone 新手机号
     * @param captcha 验证码
     * @return 修改手机号后生成或更新的账户令牌
     */
    public String changePhone(Account account, String newPhone, String captcha) {
        // 验证验证码
        checkCaptcha(account.getPhoneNumber(), captcha);
        // 更新手机号并保存账户信息
        account.setPhoneNumber(newPhone);
        return accountRepository.saveAndFlush(account).getAccountToken();
    }

    /**
     * 用户找回密码方法
     * 通过手机号、验证码和新密码，更新账户密码
     *
     * @param phone 手机号码
     * @param captcha 验证码
     * @param newPassWord 新密码
     * @return 更新密码后生成或更新的账户令牌
     */
    public String findPassword(String phone, String captcha, String newPassWord) {
        // 验证验证码
        checkCaptcha(phone, captcha);
        // 根据手机号查找账户，如果不存在则抛出异常
        Account account = accountRepository.findByPhoneNumber(phone).orElseThrow(() -> new LLException("该手机号不存在"));
        // 更新密码并保存账户信息
        account.updatePassword(newPassWord);
        return accountRepository.saveAndFlush(account).getAccountToken();
    }

    /**
     * 更新账户信息方法
     * 更新账户关联的用户信息
     *
     * @param account 用户账户
     * @param user 更新后的用户信息
     * @return 更新用户信息后生成或更新的账户令牌
     */
    public String updateAccount(Account account, AccountUser user) {
        // 保存并刷新用户信息
        userRepository.saveAndFlush(user);
        // 保存并刷新账户信息并返回令牌
        return accountRepository.saveAndFlush(account).getAccountToken();
    }
    /**
     * 发送验证码到指定手机号
     *
     * @param phone 手机号
     * @throws LLException 验证码已发送或发送失败时抛出异常
     */
    public void sendCaptcha(String phone) {
        if (captchaService.isExist(phone)) {
            throw new LLException("验证码已发送，请稍后再试");
        }
        Captcha captcha = captchaService.insertCaptchaForPhone(phone);
        if (!smsService.sendSMS(captcha.getPhone(), captcha.getCode())) {
            throw new LLException("验证码发送失败");
        }
    }

    /**
     * 通过密码生成登录令牌
     *
     * @param account    用户账户
     * @param passWord   用户输入的密码
     * @param accessIp   客户端访问IP
     * @return AccountToken 登录令牌
     * @throws LLException 密码校验错误时抛出异常
     */
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

    /**
     * 通过验证码生成登录令牌
     *
     * @param account    用户账户
     * @param captcha    用户输入的验证码
     * @param accessIp   客户端访问IP
     * @return AccountToken 登录令牌
     * @throws LLException 验证码错误或用户已被禁用时抛出异常
     */
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
     * 根据登录类型和登录键验证用户
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

    /**
     * 验证验证码的正确性
     *
     * @param phone   手机号
     * @param captcha 验证码
     * @throws LLException 验证码错误时抛出异常
     */
    private void checkCaptcha(String phone, String captcha) {
        if (!captchaService.validateCaptcha(phone, captcha)) {
            throw new LLException("验证码错误");
        }
    }


}

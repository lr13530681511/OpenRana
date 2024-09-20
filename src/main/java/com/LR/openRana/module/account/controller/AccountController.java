package com.LR.openRana.module.account.controller;

import com.LR.openRana.common.LLException;
import com.LR.openRana.common.R;
import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.AccountToken;
import com.LR.openRana.module.account.LoginType;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.service.AccountService;
import com.LR.openRana.utils.DataCheckerUtils;
import com.LR.openRana.utils.IPUtils;
import com.LR.openRana.utils.JSONUtils;
import com.LR.openRana.utils.MapUtils;
import com.LR.openRana.utils.bean.CurrentThreadUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping(value = "account")
public class AccountController {

    private final static RoleType[] allRoleTypes = RoleType.values();

    private AccountService service;

    private CurrentThreadUtils utils;

    @Autowired
    public AccountController(AccountService service, CurrentThreadUtils utils) {
        this.service = service;
        this.utils = utils;
    }

    /**
     * 用户登录方法
     * 使用不同的登录方式对用户进行登录验证
     *
     * @param params 包含登录所需的参数，如登录键、登录值和登录类型
     * @param request HTTP请求对象，用于获取用户IP
     * @return 登录成功后的结果，包含登录令牌
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public R login(@RequestBody HashMap<String, Object> params, HttpServletRequest request) {
        // 通过服务层的login方法进行登录验证，然后构建AccountToken对象并返回
        return R.ok(MapUtils.putS("result",
                new AccountToken.AccountTokenBuilder().token(
                        service.login(params.get("loginKey").toString(),
                                params.get("loginValue").toString(),
                                LoginType.fromValue(params.get("loginType").toString()),
                                IPUtils.getIpAddr(request))).build()));
    }

    /**
     * 手机号登录方法
     * 通过手机号和验证码对用户进行登录验证
     *
     * @param phone 用户的手机号
     * @param captcha 用户输入的验证码
     * @param accessIp 用户的访问IP
     * @return 登录成功后的结果，包含登录令牌
     */
    @RequestMapping(value = "loginByPhone", method = RequestMethod.POST)
    public R loginByPhone(String phone, String captcha, String accessIp) {
        // 通过服务层的loginByPhone方法进行登录验证，然后构建AccountToken对象并返回
        return R.ok(MapUtils.putS("result",
                service.loginByPhone(phone, captcha, accessIp)));
    }

    /**
     * 用户注册方法
     * 验证手机号和密码格式后，进行用户注册
     *
     * @param params 包含注册所需的参数，如手机号、密码、验证码和用户名
     * @return 注册成功后的结果，包含登录令牌
     * @throws LLException 手机号格式错误或密码长度不足6位时抛出异常
     */
    @RequestMapping(value = "sign", method = RequestMethod.POST)
    public R sign(@RequestBody HashMap<String, Object> params) {
        // 检查手机号和密码格式，如果不符合要求则抛出异常
        if (!DataCheckerUtils.isPhoneNumber(params.get("phone").toString()) || !DataCheckerUtils.isEnoughLength(params.get("passwd").toString(), 6)) {
            throw new LLException("手机号码格式错误或密码长度不足6位");
        }
        // 注册用户并返回AccountToken对象
        return R.ok(MapUtils.putS("result", new AccountToken.AccountTokenBuilder().token(
                service.sign(
                        params.get("phone").toString(),
                        params.get("passwd").toString(),
                        params.get("captcha").toString(),
                        params.get("userName").toString())).build()));
    }

    /**
     * 发送验证码方法
     * 验证手机号格式后，发送验证码
     *
     * @param params 包含手机号的参数
     * @throws LLException 手机号格式错误时抛出异常
     */
    @RequestMapping(value = "sendCaptcha", method = RequestMethod.POST)
    public R sendCaptcha(@RequestBody HashMap<String, Object> params) {
        // 检查手机号格式，如果不符合要求则抛出异常
        if (!DataCheckerUtils.isPhoneNumber(params.get("phone").toString())) {
            throw new LLException("手机号码格式错误");
        }
        // 发送验证码并返回结果
        service.sendCaptcha(params.get("phone").toString());
        return R.ok(MapUtils.putS("result", "验证码发送成功"));
    }

    /**
     * 退出登录方法
     *
     * @param request HTTP请求对象，用于获取登录状态
     * @return 退出登录后的结果
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public R logout(HttpServletRequest request) {
        // 执行退出登录操作并返回结果
        return R.ok(MapUtils.putS("result", service.logout(request)));
    }

    /**
     * 获取当前用户权限方法
     *
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 当前用户的权限信息
     */
    @RequestMapping(value = "self", method = RequestMethod.POST)
    public R currentAccount(HttpServletRequest request) {
        // 获取当前用户的权限类型并返回
        return R.ok(MapUtils.putS("result", utils.getCurrentRoleType(request)));
    }

    /**
     * 修改密码方法
     *
     * @param params 包含旧密码、新密码和验证码的参数
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 修改密码成功后的结果，包含新的登录令牌
     */
    @RequestMapping(value = "changePasswd", method = RequestMethod.POST)
    public R changePassword(@RequestBody HashMap<String, Object> params, HttpServletRequest request) {
        // 获取当前用户并进行密码修改操作，然后构建AccountToken对象并返回
        Account account = utils.getCurrentAccount(request);
        String oldPass = params.get("oldPass").toString();
        String newPass = params.get("newPass").toString();
        String captcha = params.get("captcha").toString();
        return R.ok(MapUtils.putS("result",
                new AccountToken.AccountTokenBuilder().token(
                        service.changePassword(account, oldPass, newPass, captcha)).build()));
    }

    /**
     * 修改手机号方法
     *
     * @param params 包含新的手机号和验证码的参数
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 修改手机号成功后的结果，包含新的登录令牌
     */
    @RequestMapping(value = "changePhone", method = RequestMethod.POST)
    public R changePhone(@RequestBody HashMap<String, Object> params, HttpServletRequest request) {
        // 获取当前用户并进行手机号修改操作，然后构建AccountToken对象并返回
        Account account = utils.getCurrentAccount(request);
        String phone = params.get("newPhone").toString();
        String captcha = params.get("captcha").toString();
        return R.ok(MapUtils.putS("result", new AccountToken.AccountTokenBuilder().token(
                service.changePhone(account, phone, captcha)).build()));
    }

    /**
     * 找回密码方法
     *
     * @param params 包含手机号、验证码、新密码和确认密码的参数
     * @return 找回密码成功后的结果，包含新的登录令牌
     * @throws LLException 两次密码不一致时抛出异常
     */
    @RequestMapping(value = "findPasswd", method = RequestMethod.POST)
    public R findPassWord(@RequestBody HashMap<String, Object> params) {
        // 验证新密码和确认密码是否一致，如果不一致则抛出异常
        String phone = params.get("phone").toString();
        String captcha = params.get("captcha").toString();
        String newPasswd = params.get("newPasswd").toString();
        String istNewPasswd = params.get("istNewPasswd").toString();
        if (!istNewPasswd.equals(newPasswd)) {
            throw new LLException("两次密码不一致");
        }
        // 执行找回密码操作并返回AccountToken对象
        return R.ok(MapUtils.putS("result", new AccountToken.AccountTokenBuilder().token(
                service.findPassword(phone, captcha, newPasswd)).build()));
    }

    /**
     * 修改用户信息方法
     *
     * @param params 包含要更新的用户对象的参数
     * @return 修改用户信息成功后的结果，包含新的登录令牌
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public R updateAccount(@RequestBody HashMap<String, Object> params) {
        // 将参数中的账户信息更新到系统中，然后构建AccountToken对象并返回
        Account account = JSONUtils.toJavaObject(params.get("account").toString(), Account.class);
        return R.ok(MapUtils.putS("result", new AccountToken.AccountTokenBuilder().token(
                service.updateAccount(account, account.getUser())).build()));
    }
}

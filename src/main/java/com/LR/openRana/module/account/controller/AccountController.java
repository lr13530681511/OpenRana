package com.LR.openRana.module.account.controller;


import com.LR.openRana.common.LLException;
import com.LR.openRana.common.R;
import com.LR.openRana.config.PermissionRequired;
import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.AccountToken;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.service.AccountService;
import com.LR.openRana.module.account.vo.ChangeVO;
import com.LR.openRana.module.account.vo.LoginVO;
import com.LR.openRana.utils.DataCheckerUtils;
import com.LR.openRana.utils.IPUtils;
import com.LR.openRana.utils.MapUtils;
import com.LR.openRana.utils.bean.CurrentThreadUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
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

    @PermissionRequired(permissionName = "登录", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "login")
    public R login(@RequestBody LoginVO vo, HttpServletRequest request) {
        return R.ok(MapUtils.putS("result", service.login(
                vo.getLoginKey(),
                vo.getLoginValue(),
                vo.getLoginType(),
                IPUtils.getIpAddr(request))));
    }

    @PermissionRequired(permissionName = "手机号登录", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "loginByPhone")
    public R loginByPhone(@RequestBody LoginVO vo, HttpServletRequest request) {
        return R.ok(MapUtils.putS("result",
                service.loginByPhone(vo.getPhone(), vo.getCaptcha(), IPUtils.getIpAddr(request))));
    }

    @PermissionRequired(permissionName = "注册", appName = "用户账户", moduleName = "account")
    @RequestMapping(value = "sign", method = RequestMethod.POST)
    public R sign(@RequestBody LoginVO vo) {
        if (!DataCheckerUtils.isPhoneNumber(vo.getPhone()) || !DataCheckerUtils.isEnoughLength(vo.getPasswd(), 6)) {
            throw new LLException("手机号码格式错误或密码长度不足6位");
        }
        return R.ok(MapUtils.putS("result", service.sign(
                vo.getPhone(),
                vo.getPasswd(),
                vo.getCaptcha(),
                vo.getUserName())));
    }

    @PermissionRequired(permissionName = "发送验证码", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "sendCaptcha")
    public R sendCaptcha(@RequestBody LoginVO vo) {
        if (!DataCheckerUtils.isPhoneNumber(vo.getPhone())) {
            throw new LLException("手机号码格式错误");
        }
        service.sendCaptcha(vo.getPhone());
        return R.ok(MapUtils.putS("result", "验证码发送成功"));
    }

    @PermissionRequired(value = RoleType.USER, permissionName = "退出登录",  appName = "用户账户", moduleName = "account")
    @PostMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        return R.ok(MapUtils.putS("result", service.logout(request)));
    }

    @PermissionRequired(value = RoleType.USER, permissionName = "我的权限", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "self")
    public R currentAccount(HttpServletRequest request) {
        return R.ok(MapUtils.putS("result", utils.getCurrentRoleType(request)));
    }

    @PermissionRequired(value = RoleType.USER, permissionName = "修改密码", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "changePasswd")
    public R changePassword(@RequestBody ChangeVO vo, HttpServletRequest request) {
        Account account = utils.getCurrentAccount(request);
        return R.ok(MapUtils.putS("result", service.changePassword(
                account,
                vo.getOldPasswd(),
                vo.getNewPasswd(),
                vo.getCaptcha())));
    }

    @PermissionRequired(value = RoleType.USER, permissionName = "修改手机号", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "changePhone")
    public R changePhone(@RequestBody ChangeVO vo, HttpServletRequest request) {
        Account account = utils.getCurrentAccount(request);
        return R.ok(MapUtils.putS("result", service.changePhone(account, vo.getNewPhone(), vo.getCaptcha())));
    }

    @PermissionRequired(permissionName = "找回密码",  appName = "用户账户", moduleName = "account")
    @PostMapping(value = "findPasswd")
    public R findPassWord(@RequestBody ChangeVO vo) {
        if (!vo.getIstNewPasswd().equals(vo.getNewPasswd())) {
            throw new LLException("两次密码不一致");
        }
        return R.ok(MapUtils.putS("result", new AccountToken.AccountTokenBuilder().token(
                service.findPassword(vo.getPhone(), vo.getCaptcha(), vo.getNewPasswd())).build()));
    }

    @PermissionRequired(value = RoleType.USER, permissionName = "修改信息", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "update")
    public R updateAccount(@RequestBody ChangeVO vo) {
        return R.ok(MapUtils.putS("result", new AccountToken.AccountTokenBuilder().token(
                service.updateAccount(vo.getAccount(), vo.getAccount().getUser())).build()));
    }

    @PermissionRequired(value = RoleType.ADMIN, permissionName = "绑定角色", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "bindRole")
    public R bindRole(@RequestBody Map<String, Long> body, HttpServletRequest request){
        Account account = utils.getCurrentAccount(utils.getCurrentToken(request));
        return R.ok(service.bindRole(account, body.get("roleId")));
    }

    @PermissionRequired(value = RoleType.ADMIN, permissionName = "解绑角色", appName = "用户账户", moduleName = "account")
    @PostMapping(value = "unBindRole")
    public R unBindRole(@RequestBody Map<String, Long> body, HttpServletRequest request){
        Account account = utils.getCurrentAccount(utils.getCurrentToken(request));
        return R.ok(service.unBindRole(account, body.get("roleId")));
    }
}

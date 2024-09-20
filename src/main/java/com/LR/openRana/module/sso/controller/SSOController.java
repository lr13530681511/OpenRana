package com.LR.openRana.module.sso.controller;

import com.LR.openRana.common.R;
import com.LR.openRana.config.PermissionRequired;
import com.LR.openRana.config.RoleFilter;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.sso.CheckRoleResult;
import com.LR.openRana.module.sso.PermissionRegistrationResult;
import com.LR.openRana.module.sso.SSOLoginRequests;
import com.LR.openRana.module.sso.SSOLoginVO;
import com.LR.openRana.module.sso.service.PermissionRegistrationMachine;
import com.LR.openRana.module.sso.service.SSOService;
import com.LR.openRana.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 单点登录（SSO）控制器类
 */
@Slf4j
@Controller
@RequestMapping(value = "SSO")
public class SSOController {

    // 注册权限机服务
    private PermissionRegistrationMachine registrationMachine;
    // SSO服务
    private SSOService service;

    /**
     * SSO控制器构造函数
     *
     * @param registrationMachine 权限注册机实例
     * @param service             SSO服务实例
     */
    @Autowired
    public SSOController(PermissionRegistrationMachine registrationMachine, SSOService service) {
        this.registrationMachine = registrationMachine;
        this.service = service;
    }

    /**
     * 处理登录请求
     *
     * @param requests 登录请求体
     * @return 登录结果
     */
    @PostMapping("login")
    @ResponseBody
    public R login(@RequestBody SSOLoginRequests requests) {
        service.login(requests);
        if (requests.getIsSuccess()) {
            return R.ok(MapUtils.putS("result", new SSOLoginVO(requests.getAppName(), requests.getRedirectUrl(), requests.getToken())));
        } else {
            return R.error(requests.getMsg());
        }
    }

    /**
     * 处理权限注册请求
     *
     * @param requests 权限注册请求体
     * @return 权限注册结果
     */
    @ResponseBody
    @PermissionRequired(value = RoleType.GUEST, appName = "用户权限系统", moduleName = "account", permissionName = "权限注册")
    @RequestMapping(value = "permissionRegistration", method = RequestMethod.POST)
    public PermissionRegistrationResult permissionRegistration(@RequestBody PermissionRegistrationResult requests) {
        try {
            return registrationMachine.register(requests);
        } catch (Exception e) {
            return new PermissionRegistrationResult.Builder()
                    .withResult(false)
                    .withMsg(e.getMessage())
                    .build();
        }
    }

    /**
     * 检查用户角色
     *
     * @param requests 角色检查请求体
     * @return 角色检查结果
     */
    @ResponseBody
    @RequestMapping(value = "checkRole", method = RequestMethod.POST)
    public CheckRoleResult checkRole(@RequestBody CheckRoleResult requests) {
        try {
            return new RoleFilter().remoteCheckPermissions(requests);
        } catch (Exception e) {
            return new CheckRoleResult.Builder()
                    .withResult(false)
                    .withMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * 跳转到SSO登录页面
     *
     * @param appName     应用名称
     * @param redirectUrl 登录后重定向URL
     * @param model       模型对象，用于传递数据到视图
     * @return 登录页面视图名称
     */
    @GetMapping("ssoLogin")
    public String loginPage(@RequestParam String appName,
                            @RequestParam String redirectUrl,
                            Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("redirectUrl", redirectUrl);
        return "ssoLogin";
    }

}

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

@Slf4j
@Controller
@RequestMapping(value = "SSO")
public class SSOController {


    private PermissionRegistrationMachine registrationMachine;
    private SSOService service;

    @Autowired
    public SSOController(PermissionRegistrationMachine registrationMachine, SSOService service) {
        this.registrationMachine = registrationMachine;
        this.service = service;
    }

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

    @ResponseBody
    @RequestMapping(value = "checkRole", method = RequestMethod.POST)
    public CheckRoleResult checkRole(@RequestBody CheckRoleResult requests) {
        return new RoleFilter().checkPermissions(requests);
    }

    @GetMapping("ssoLogin")
    public String loginPage(@RequestParam String appName,
                            @RequestParam String redirectUrl,
                            Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("redirectUrl", redirectUrl);
        return "ssoLogin";
    }

}

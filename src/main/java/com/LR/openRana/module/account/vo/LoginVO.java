package com.LR.openRana.module.account.vo;

import com.LR.openRana.module.account.LoginType;
import lombok.Data;

@Data
public class LoginVO {

    private String loginKey;

    private String loginValue;

    private String userName;

    private String passwd;

    private String phone;

    private String captcha;

    private LoginType loginType;


}

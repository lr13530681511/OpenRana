package com.LR.openRana.module.account.vo;

import com.LR.openRana.module.account.Account;
import lombok.Data;

@Data
public class ChangeVO {

    private Account account;

    private String captcha;

    private String oldPasswd;

    private String newPasswd;

    private String istNewPasswd;

    private String phone;

    private String newPhone;

    private String oldEmail;

    private String newEmail;
}

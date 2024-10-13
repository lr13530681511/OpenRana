package com.LR.openRana.module.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SSOLoginVO {

    private String appName;

    private String redirectUrl;

    private String ssoToken;
}

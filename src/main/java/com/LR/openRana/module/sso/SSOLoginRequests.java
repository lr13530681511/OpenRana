package com.LR.openRana.module.sso;

import com.LR.openRana.module.account.LoginType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class SSOLoginRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String appName;

    private String redirectUrl;

    private LoginType loginType;

    private String loginK;

    private String loginV;

    private String LoginServer;

    private LocalDateTime requestsTime;

    private Boolean isSuccess;

    @Column(columnDefinition = "TEXT")
    private String token;

    private String msg;

    // Builder class for SSORequests
    public static class Builder {
        private SSOLoginRequests ssoLoginRequests = new SSOLoginRequests();

        public Builder uid(Long uid) {
            ssoLoginRequests.setUid(uid);
            return this;
        }

        public Builder redirectUrl(String redirectUrl) {
            ssoLoginRequests.setRedirectUrl(redirectUrl);
            return this;
        }

        public Builder loginType(LoginType loginType) {
            ssoLoginRequests.setLoginType(loginType);
            return this;
        }

        public Builder loginK(String loginK) {
            ssoLoginRequests.setLoginK(loginK);
            return this;
        }

        public Builder loginV(String loginV) {
            ssoLoginRequests.setLoginV(loginV);
            return this;
        }

        public Builder loginServer(String loginServer) {
            ssoLoginRequests.setLoginServer(loginServer);
            return this;
        }

        public Builder requestsTime(LocalDateTime requestsTime) {
            ssoLoginRequests.setRequestsTime(requestsTime);
            return this;
        }

        public Builder isSuccess(Boolean isSuccess) {
            ssoLoginRequests.setIsSuccess(isSuccess);
            return this;
        }

        public Builder token(String token) {
            ssoLoginRequests.setToken(token);
            return this;
        }

        public Builder msg(String msg) {
            ssoLoginRequests.setMsg(msg);
            return this;
        }

        public SSOLoginRequests build() {
            return ssoLoginRequests;
        }
    }

}

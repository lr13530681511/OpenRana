package com.LR.openRana.module.account;

import com.LR.openRana.common.LLException;
import com.LR.openRana.common.SpringContextUtils;
import com.LR.openRana.module.account.repository.AccountRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account_token")
public class AccountToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String token;

    @JsonIgnore
    private long expiredTime;

    @JsonIgnore
    private long accountUid;

    @JsonIgnore
    public boolean isExpired() {
        return System.currentTimeMillis() > expiredTime;
    }


    @JsonIgnore
    public Account getAccount() {
        try {
            return SpringContextUtils.getBean(AccountRepository.class).getReferenceById(this.accountUid);
        } catch (Exception e) {
            throw new LLException("账户已删除");
        }

    }

    public AccountToken() {
        // 初始化代码（如果需要的话）
    }


    private AccountToken(AccountTokenBuilder builder) {
        this.uid = builder.uid;
        this.token = builder.token;
        this.expiredTime = builder.expiredTime;
        this.accountUid = builder.accountUid;
    }

    // Builder类
    public static class AccountTokenBuilder {
        private Long uid;
        private String token;
        private long expiredTime;
        private long accountUid;

        public AccountTokenBuilder() {
        }

        public AccountTokenBuilder uid(Long uid) {
            this.uid = uid;
            return this;
        }

        public AccountTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AccountTokenBuilder expiredTime(long expiredTime) {
            this.expiredTime = expiredTime;
            return this;
        }

        public AccountTokenBuilder accountUid(long accountUid) {
            this.accountUid = accountUid;
            return this;
        }

        public AccountToken build() {
            return new AccountToken(this);
        }
    }


}

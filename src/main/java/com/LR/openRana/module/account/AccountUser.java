package com.LR.openRana.module.account;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "account_user")
public class AccountUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String name;

    private LocalDate birth;

    private long accountUid;

    private String avatar;

    public AccountUser() {
    }

    // Add a constructor for AccountUserBuilder
    public AccountUser(AccountUserBuilder builder) {
        this.uid = builder.uid;
        this.name = builder.name;
        this.birth = builder.birth;
        this.accountUid = builder.accountUid;
    }

    // Getters and Setters if needed
    // ...


    // Add the builder class
    public static class AccountUserBuilder {

        private Long uid;
        private String name;
        private LocalDate birth;
        private long accountUid;

        public AccountUserBuilder() {
        }

        public AccountUserBuilder uid(Long uid) {
            this.uid = uid;
            return this;
        }

        public AccountUserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AccountUserBuilder birth(LocalDate birth) {
            this.birth = birth;
            return this;
        }

        public AccountUserBuilder accountUid(long accountUid) {
            this.accountUid = accountUid;
            return this;
        }

        public AccountUser build() {
            return new AccountUser(this);
        }
    }
}

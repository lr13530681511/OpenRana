package com.LR.openRana.module.account;

import com.LR.openRana.module.account.repository.AccountRepository;
import com.LR.openRana.utils.DataFactoryUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Account类表示一个用户账户实体，用于管理用户的基本信息、登录验证和角色权限。
 * 它使用了Lombok的@Data注解以提供自动化的getter和setter方法。
 * 此类中的字段包括账户的唯一标识、用户名、电话号码、电子邮件、密码（@JsonIgnore以避免在JSON序列化时暴露）、
 * 用户对象、创建时间、激活状态、角色列表、账户令牌以及是否未过期的标记。
 * 此外，还包含了微信登录的相关信息，如微信开放ID、访问令牌和刷新令牌。
 */
@Data
@Entity
@Table(name = "account_account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 账户的唯一标识符，由数据库自动生成

    private String userName; // 用户名

    private String phoneNumber; // 电话号码

    private String email; // 电子邮件地址

    @JsonIgnore
    private String passWord; // 用户密码，@JsonIgnore防止在JSON输出时泄露

    @JsonIgnore
    private String salt;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String accountToken; // 账户令牌

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private AccountUser user; // 关联的用户实体，采用级联操作

    @JsonIgnore
    private LocalDateTime createTime; // 账户创建时间

    private Boolean isActive; // 账户是否激活

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<AccountRole> roles; // 账户的角色列表

    private Boolean isNoExpired; // 账户令牌是否未过期


    // 微信登录相关信息
    private String wechatOpenId; // 微信开放ID

    private String wechatAccessToken; // 微信访问令牌

    private String refreshToken; // 刷新令牌


    public Account() {
    }

    public Set<RoleType> getRoleTypes() {
        return roles.stream()
                .map(AccountRole::getRoleType)
                .collect(Collectors.toSet());
    }

    public RoleType getMaxRole() {
        return getRoleTypes().stream()
                .max(RoleType::compareTo)
                .orElse(RoleType.USER);
    }

    public Set<AccountPermission> getPermissions() {
        return roles.stream()
                .flatMap(r -> r.getPermissions().stream())
                .distinct()
                .collect(Collectors.toCollection(HashSet::new));
    }


    public boolean isAdmin() {
        return roles.stream().anyMatch(r -> "admin".equalsIgnoreCase(r.getRoleType().toString()) || "op".equalsIgnoreCase(r.getRoleType().toString()));
    }

    public void bindRole(AccountRole role) {
        roles.add(role);
    }

    public void unBindRole(AccountRole role) {
        roles.remove(role);
    }

    public void updatePassword(String plainPassword) {
        setPassWord(DataFactoryUtils.addSalt(plainPassword, salt));
    }

    public boolean checkPassword(String plainPassword) {
        return this.getPassWord().equals(DataFactoryUtils.addSalt(plainPassword, salt));
    }

    public AccountToken loginBySMSCode(AccountRepository repository, String uuid, String captcha,
                                       long expiredTime, String accessIp, String source) {
        // TODO 短信登录
////        if (LoginCounter.overErrorTime(getUserId())) {
////            throw new RRException("尝试登录次数失败过多，请稍后重试");
////        }
//        if (isNeedImageCheck()) {
//            var sysCaptchaService = SpringContextUtils.getBean(SysCaptchaService.class);
//            if (!sysCaptchaService.validate(uuid, captcha)) {
//                LoginCounter.whenError(userId, accessIp);
//                throw new RRException("验证码校验失败");
//            }
//        }
//        AccessToken accessToken = new AccessToken();
//        accessToken.setAccountUid(this.userId);
//        accessToken.setSource(source);
//        accessToken.setExpiredTime(Objects.nonNull(expiredTime) ? expiredTime
//                : LocalDateTime.now().plus(defaultExpiredTimeMs, MILLIS)
//                .toEpochSecond(ZoneOffset.ofHours(8)) * 1000);
//        accessToken = repository.save(accessToken);
//        LoginCounter.whenSuccessfulLogin(this.userId, accessIp);
//        return accessToken;
//    }
        return null;
    }

    // 私有构造器，防止直接实例化
    private Account(AccountBuilder builder) {
        this.uid = builder.uid;
        this.userName = builder.userName;
        this.phoneNumber = builder.phoneNumber;
        this.email = builder.email;
        this.wechatOpenId = builder.wechatOpenId;
        this.passWord = builder.passWord;
        this.salt = builder.salt;
    }

    public boolean isActive() {
        return isActive;
    }

    // Builder静态内部类
    public static class AccountBuilder {
        private Long uid;
        private String userName;
        private String phoneNumber;
        private String email;
        private String wechatOpenId;
        private String passWord;
        private String salt;


        public AccountBuilder() {
        }

        public AccountBuilder uid(Long uid) {
            this.uid = uid;
            return this;
        }

        public AccountBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public AccountBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public AccountBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AccountBuilder wechatOpenId(String wechatOpenId) {
            this.wechatOpenId = wechatOpenId;
            return this;
        }

        public AccountBuilder passWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public AccountBuilder salt(String salt) {
            this.salt = salt;
            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }


}

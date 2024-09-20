package com.LR.openRana.utils.bean;

import com.LR.openRana.common.LLException;
import com.LR.openRana.common.SpringContextUtils;
import com.LR.openRana.module.account.Account;
import com.LR.openRana.module.account.RoleType;
import com.LR.openRana.module.account.repository.AccountTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * 用于获取当前线程中的用户信息和角色类型的工具类
 */
@Component
public class CurrentThreadUtils {

    /**
     * 从请求中获取当前的Token
     *
     * @param request HTTP请求对象，用于获取Token信息
     * @return 当前请求的Token字符串
     */
    public String getCurrentToken(HttpServletRequest request) {
        return request.getHeader("Token");
    }

    /**
     * 通过Token字符串获取当前的用户账户信息
     *
     * @param tokenString Token字符串，用于识别用户
     * @return 当前用户的Account对象
     * @throws LLException 如果Token无效，则抛出异常
     */
    public Account getCurrentAccount(String tokenString) {
        AccountTokenRepository repository = getRepository();
        if (!repository.existsByToken(tokenString)) {
            throw new LLException("当前访问角色为访客");
        }
        return repository.findByToken(tokenString).get().getAccount();
    }

    /**
     * 通过Token字符串获取当前用户的角色类型集合
     *
     * @param tokenString Token字符串，用于识别用户
     * @return 当前用户的角色类型集合
     */
    public Set<RoleType> getCurrentRoleType(String tokenString) {
        AccountTokenRepository repository = getRepository();
        if (!repository.existsByToken(tokenString)) {
            return Collections.singleton(RoleType.GUEST);
        }
        return repository.findByToken(tokenString).get().getAccount().getRoleTypes();
    }

    /**
     * 通过Token字符串获取当前用户的最高角色类型
     *
     * @param tokenString Token字符串，用于识别用户
     * @return 当前用户的最高RoleType
     */
    public RoleType getCurrentMaxRoleType(String tokenString) {
        AccountTokenRepository repository = getRepository();
        if (!repository.existsByToken(tokenString)) {
            return RoleType.GUEST;
        }
        return repository.findByToken(tokenString).get().getAccount().getMaxRole();
    }

    /**
     * 从请求中获取当前用户的角色类型集合
     *
     * @param request HTTP请求对象，用于获取Token信息
     * @return 当前用户的角色类型集合
     */
    public Set<RoleType> getCurrentRoleType(HttpServletRequest request) {
        if (request.getHeader("Token") == null) {
            return Collections.singleton(RoleType.GUEST);
        }
        return getCurrentAccount(request).getRoleTypes();
    }

    /**
     * 从请求中获取当前的用户账户信息
     *
     * @param request HTTP请求对象，用于获取Token信息
     * @return 当前用户的Account对象
     */
    public Account getCurrentAccount(HttpServletRequest request) {
        return getCurrentAccount(request.getHeader("Token"));
    }

    /**
     * 获取AccountTokenRepository的实例
     *
     * @return AccountTokenRepository的实例
     */
    private AccountTokenRepository getRepository() {
        return SpringContextUtils.getBean(AccountTokenRepository.class);
    }

}

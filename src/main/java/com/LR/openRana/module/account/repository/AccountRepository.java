package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByPhoneNumber(String phoneNumber);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserName(String userName);

    Optional<Account> findByWechatOpenId(String wechatOpenId);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUserName(String userName);
}

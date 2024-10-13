package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {

    Optional<AccountUser> findByAccountUid(Long accountUid);

    Optional<AccountUser> findByName(String name);

    boolean existsByName(String name);
}

package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.AccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {
    void deleteByAccountUid(Long accountUid);

    void deleteByToken(String token);

    Optional<AccountToken> findByToken(String token);

    boolean existsByToken(String token);
}

package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

    AccountRole findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}


package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.AccountPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@DependsOn("AccountPermission")
@Repository
public interface AccountPermissionRepository extends JpaRepository<AccountPermission, Long> {

    Optional<AccountPermission> findByPath(String path);
}

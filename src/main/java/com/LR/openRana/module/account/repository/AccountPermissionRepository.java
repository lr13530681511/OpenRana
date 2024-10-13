package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.AccountPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//@DependsOn("AccountPermission")
@Repository
public interface AccountPermissionRepository extends JpaRepository<AccountPermission, Long>, JpaSpecificationExecutor<AccountPermission> {

    Optional<AccountPermission> findByPath(String path);

    @Query("SELECT DISTINCT a.appName FROM AccountPermission a")
    List<String> findAllAppNames();

    @Query("SELECT DISTINCT a.modelName FROM AccountPermission a")
    List<String> findAllModelNames();

    @Query("SELECT DISTINCT a.controller FROM AccountPermission a")
    List<String> findAllControllers();

    default Set<String> findAllAppNamesAsSet() {
        return new HashSet<>(findAllAppNames());
    }

    default Set<String> findAllModelNamesAsSet() {
        return new HashSet<>(findAllModelNames());
    }

    default Set<String> findAllControllersAsSet() {
        return new HashSet<>(findAllControllers());
    }
}

package com.LR.openRana.module.sso.repository;

import com.LR.openRana.module.sso.CheckRoleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckRoleResultRepository extends JpaRepository<CheckRoleResult, Long> {
}

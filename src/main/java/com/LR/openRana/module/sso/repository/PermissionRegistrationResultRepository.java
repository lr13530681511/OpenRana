package com.LR.openRana.module.sso.repository;

import com.LR.openRana.module.sso.PermissionRegistrationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRegistrationResultRepository extends JpaRepository<PermissionRegistrationResult, Long> {
}

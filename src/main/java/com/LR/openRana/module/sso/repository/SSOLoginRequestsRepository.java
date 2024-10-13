package com.LR.openRana.module.sso.repository;

import com.LR.openRana.module.sso.SSOLoginRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SSOLoginRequestsRepository extends JpaRepository<SSOLoginRequests, Long> {
}

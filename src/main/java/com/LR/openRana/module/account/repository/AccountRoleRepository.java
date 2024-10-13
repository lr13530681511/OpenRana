package com.LR.openRana.module.account.repository;

import com.LR.openRana.module.account.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AccountRoleRepository接口，用于定义与数据库交互的方法。
 * 继承自JpaRepository，添加了自定义的方法来操作AccountRole实体。
 */
@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

    /**
     * 根据角色名称查找对应的AccountRole实体。
     *
     * @param roleName 角色的名称，用于查询。
     * @return 如果找到匹配的角色，则返回对应的AccountRole实体；如果没有找到，则返回null。
     */
    AccountRole findByRoleName(String roleName);

    /**
     * 判断给定的角色名称是否存在。
     *
     * @param roleName 角色的名称，用于查询是否存在。
     * @return 如果存在匹配的角色名称，则返回true；如果不存在，则返回false。
     */
    boolean existsByRoleName(String roleName);
}


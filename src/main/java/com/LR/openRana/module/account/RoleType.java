package com.LR.openRana.module.account;


import com.LR.openRana.common.LLException;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RoleType {

    OP("op", 4),
    ADMIN("admin", 3),
    USER("user", 2),
    GUEST("guest", 1);

    private String name;
    private int level; // 添加等级字段

    RoleType(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() { // 添加获取等级的方法
        return level;
    }

    public static Set<RoleType> getAll() {
        return Set.of(values());
    }

    /**
     * 获取级别低于或等于给定角色的所有角色集合
     * 此方法通过筛选所有角色类型中的级别低于或等于指定角色级别的角色，来得到一个集合
     *
     * @param role 指定的角色，用于比较级别
     * @return 返回一个包含所有级别低于或等于给定角色的角色集合如果给定的角色为null，将返回空集合
     */
    public static Set<RoleType> getRolesBelowOrEqual(RoleType role) {
        return Stream.of(values())
                .filter(r -> r.getLevel() <= role.getLevel())
                .collect(Collectors.toSet());
    }

    /**
     * 从给定的角色集合中找出级别最高的角色
     * 此方法通过比较角色集合中每个角色的级别，找出级别最高的角色如果集合为空，将抛出异常
     *
     * @param roleTypes 角色集合，从中找出级别最高的角色
     * @return 返回级别最高的角色如果角色集合为空，将抛出LLException异常
     * @throws LLException 如果角色集合为空，表示找不到任何角色
     */
    public static RoleType getMaxLevel(Set<RoleType> roleTypes) {
        if (roleTypes.isEmpty()) {
            throw new LLException("角色列表空");
        }
        return roleTypes.stream()
                .max(Comparator.comparingInt(RoleType::getLevel)).get();
    }

}


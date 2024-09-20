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

    public static Set<RoleType> getRolesBelowOrEqual(RoleType role) {
        return Stream.of(values())
                .filter(r -> r.getLevel() <= role.getLevel())
                .collect(Collectors.toSet());
    }

    public static RoleType getMaxLevel(Set<RoleType> roleTypes) {
        if (roleTypes.isEmpty()) {
            throw new LLException("角色列表空");
        }
        return roleTypes.stream()
                .max(Comparator.comparingInt(RoleType::getLevel)).get();
    }

}


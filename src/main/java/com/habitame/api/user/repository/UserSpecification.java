package com.habitame.api.user.repository;

import com.habitame.api.user.dto.UserFilter;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

    private UserSpecification() {}

    public static Specification<UserEntity> filter(UserFilter f) {
        return Specification
                .where(f.role() != null ? hasRole(f.role()) : null)
                .and(f.isActive() != null ? isActive(f.isActive()) : null);
    }

    private static Specification<UserEntity> hasRole(Role role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    private static Specification<UserEntity> isActive(Boolean active) {
        return (root, query, cb) -> cb.equal(root.get("isActive"), active);
    }
}

package com.habitame.api.auth.security;

import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("There is no authenticated user");
        }

        if (auth.getPrincipal() instanceof UserEntity userEntity) {
            return userEntity;
        }

        throw new UnauthorizedException("The current user could not be obtained");
    }

    public static boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    public static boolean isOwnerOf(UserEntity owner) {
        return owner.getId().equals(getCurrentUserId());
    }
}

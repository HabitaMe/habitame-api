package com.habitame.api.auth.security;

import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() { }

    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserEntity userDetails) {
            return userDetails.getId();
        }

        throw new RuntimeException("Cannot get current user id");
    }

    public static UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserEntity userEntity) {
            return userEntity;
        }

        throw new RuntimeException("Cannot get current user id");
    }
}

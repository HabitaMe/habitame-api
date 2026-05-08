package com.habitame.api.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Integer id,
        String username,
        String fullName,
        String phone,
        String photoUrl,
        String email,
        String role,
        Boolean isActive,
        LocalDateTime createdAt
) {}

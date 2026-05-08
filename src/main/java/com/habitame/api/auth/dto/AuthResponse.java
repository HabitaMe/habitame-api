package com.habitame.api.auth.dto;

import com.habitame.api.user.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {}

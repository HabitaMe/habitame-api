package com.habitame.api.auth.dto;

import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserResponse user;
}
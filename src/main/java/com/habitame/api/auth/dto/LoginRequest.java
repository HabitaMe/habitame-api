package com.habitame.api.auth.dto;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {}

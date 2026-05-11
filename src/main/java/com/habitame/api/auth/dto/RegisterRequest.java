package com.habitame.api.auth.dto;

import com.habitame.api.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String username,
        @Email String email,
        @Size(min = 8) String password,
        String fullName,
        String phone,
        @NotNull Role role
) {}

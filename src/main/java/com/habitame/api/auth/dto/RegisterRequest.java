package com.habitame.api.auth.dto;

import com.habitame.api.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @Size(min = 8)
    private String password;

    private String fullName;
    private String phone;

    @NotNull
    private Role role;
}


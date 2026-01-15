package com.habitame.api.user.dto;

import java.time.LocalDateTime;

public class UserResponse {

    public Long id;
    public String username;
    public String email;
    public String role;
    public Boolean isActive;
    public LocalDateTime createdAt;
}
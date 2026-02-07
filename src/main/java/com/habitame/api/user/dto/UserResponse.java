package com.habitame.api.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    public Integer id;
    public String username;
    public String email;
    public String role;
    public Boolean isActive;
    public LocalDateTime createdAt;
}
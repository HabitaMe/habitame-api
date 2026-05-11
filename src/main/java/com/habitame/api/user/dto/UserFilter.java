package com.habitame.api.user.dto;

import com.habitame.api.user.entity.Role;

public record UserFilter(
        Role role,
        Boolean isActive
) {}

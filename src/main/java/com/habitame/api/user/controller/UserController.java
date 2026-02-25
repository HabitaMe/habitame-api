package com.habitame.api.user.controller;

import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity userEntity)) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        return UserMapper.toResponse(userEntity);
    }
}

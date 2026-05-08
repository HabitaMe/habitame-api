package com.habitame.api.user.controller;

import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.user.dto.UserRequest;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity userEntity)) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        return UserMapper.toResponse(userEntity);
    }

    @PostMapping("{idUser}/photo")
    public ResponseEntity<UserResponse> addPhoto(@PathVariable Integer idUser, @Valid MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.addPhoto(idUser, file));
    }

    @PutMapping()
    public ResponseEntity<UserResponse> updateMe(@RequestBody @Valid UserRequest request) throws IOException {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @DeleteMapping("{idUser}/photo")
    public ResponseEntity<UserResponse> deletePhoto(@PathVariable Integer idUser) throws IOException {
        return ResponseEntity.ok(userService.removePhoto(idUser));
    }
}

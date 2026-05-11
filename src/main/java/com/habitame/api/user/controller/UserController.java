package com.habitame.api.user.controller;

import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.user.dto.UserRequest;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Mi perfil", description = "Consulta y edición de los datos del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil", description = "Devuelve los datos del usuario autenticado a partir del token JWT.")
    public UserResponse getCurrentUser(Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity userEntity)) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        return UserMapper.toResponse(userEntity);
    }

    @PostMapping("{id}/photo")
    @Operation(summary = "Subir foto de perfil", description = "Sube una imagen y la asigna como foto de perfil del usuario indicado.")
    public ResponseEntity<UserResponse> addPhoto(@PathVariable Integer id, @Valid MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.addPhoto(id, file));
    }

    @PutMapping()
    @Operation(summary = "Actualizar mi perfil", description = "Actualiza el nombre y el teléfono del usuario autenticado.")
    public ResponseEntity<UserResponse> updateMe(@RequestBody @Valid UserRequest request) throws IOException {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @DeleteMapping("{id}/photo")
    @Operation(summary = "Eliminar foto de perfil", description = "Borra la foto de perfil del usuario indicado.")
    public ResponseEntity<UserResponse> deletePhoto(@PathVariable Integer id) throws IOException {
        return ResponseEntity.ok(userService.removePhoto(id));
    }
}

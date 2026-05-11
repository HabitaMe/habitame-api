package com.habitame.api.user.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.user.dto.UserActiveRequest;
import com.habitame.api.user.dto.UserFilter;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Usuarios (Admin)", description = "Gestión de usuarios de la plataforma. Solo accesible para ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios con filtros opcionales por rol e estado (activo/inactivo).")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(new UserFilter(role, isActive), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver usuario por ID", description = "Devuelve los datos completos de un usuario concreto.")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Activar o desactivar usuario", description = "Cambia el estado activo/inactivo de un usuario. Un usuario inactivo no puede iniciar sesión.")
    public ResponseEntity<UserResponse> setActive(
            @PathVariable Integer id,
            @RequestBody UserActiveRequest request) {
        return ResponseEntity.ok(userService.setActive(id, request.active()));
    }
}

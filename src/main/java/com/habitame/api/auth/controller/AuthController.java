package com.habitame.api.auth.controller;

import com.habitame.api.auth.dto.AuthResponse;
import com.habitame.api.auth.dto.LoginRequest;
import com.habitame.api.auth.dto.RefreshRequest;
import com.habitame.api.auth.dto.RegisterRequest;
import com.habitame.api.auth.service.AuthService;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y gestión de sesión mediante JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta nueva con rol ARRENDADOR por defecto.")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserMapper.toResponse(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un access token (1h) y un refresh token (24h).")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Usa el refresh token para obtener un nuevo access token sin volver a hacer login.")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cerrar sesión", description = "Invalida el refresh token. El access token sigue siendo válido hasta que expire.")
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
    }
}

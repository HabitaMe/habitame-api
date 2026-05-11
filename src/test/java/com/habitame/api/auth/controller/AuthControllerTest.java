package com.habitame.api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.dto.AuthResponse;
import com.habitame.api.auth.dto.LoginRequest;
import com.habitame.api.auth.dto.RefreshRequest;
import com.habitame.api.auth.dto.RegisterRequest;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.auth.service.AuthService;
import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;


    @Test
    void register_ShouldReturnCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "juanito", "juan@mail.com", "password123", "Juan García", "600000000", Role.ARRENDADOR
        );

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1);
        savedUser.setUsername("juanito");
        savedUser.setEmail("juan@mail.com");
        savedUser.setFullName("Juan García");
        savedUser.setRole(Role.ARRENDADOR);
        savedUser.setIsActive(true);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(authService.register(any(RegisterRequest.class))).thenReturn(savedUser);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("juanito"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.role").value("ARRENDADOR"));
    }

    @Test
    void register_WithoutUsername_ShouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "", "juan@mail.com", "password123", "Juan García", null, Role.ARRENDADOR
        );

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithShortPassword_ShouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "juanito", "juan@mail.com", "corta", "Juan García", null, Role.ARRENDADOR
        );

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithDuplicateUsername_ShouldReturn409() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "juanito", "juan@mail.com", "password123", "Juan García", null, Role.ARRENDADOR
        );

        when(authService.register(any())).thenThrow(new DuplicateResourceException("Username o email ya existente"));

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }


    @Test
    void login_ShouldReturnTokens() throws Exception {
        LoginRequest request = new LoginRequest("juanito", "password123");

        UserResponse userResponse = new UserResponse(
                1, "juanito", "Juan García", null, null, "juan@mail.com", "ARRENDADOR", true, LocalDateTime.now()
        );
        AuthResponse response = new AuthResponse("access-token", "refresh-token", "Bearer", 3600, userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.username").value("juanito"));
    }

    @Test
    void login_WithBadCredentials_ShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest("juanito", "wrongpass");

        when(authService.login(any())).thenThrow(new UnauthorizedException("Credenciales incorrectas"));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }


    @Test
    void logout_ShouldReturnNoContent() throws Exception {
        RefreshRequest request = new RefreshRequest("valid-refresh-token");

        doNothing().when(authService).logout(any(RefreshRequest.class));

        mockMvc.perform(post("/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_WithInvalidToken_ShouldReturn401() throws Exception {
        RefreshRequest request = new RefreshRequest("token-caducado");

        doThrow(new UnauthorizedException("Refresh token no válido")).when(authService).logout(any());

        mockMvc.perform(post("/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

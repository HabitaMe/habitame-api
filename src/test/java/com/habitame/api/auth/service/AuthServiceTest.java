package com.habitame.api.auth.service;

import com.habitame.api.auth.dto.AuthResponse;
import com.habitame.api.auth.dto.LoginRequest;
import com.habitame.api.auth.dto.RefreshRequest;
import com.habitame.api.auth.dto.RegisterRequest;
import com.habitame.api.auth.entity.RefreshTokenEntity;
import com.habitame.api.auth.repository.RefreshTokenRepository;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    // ------------------- register -------------------

    @Test
    void register_ShouldSaveUserWithHashedPassword() {
        RegisterRequest request = new RegisterRequest(
                "juanito", "juan@mail.com", "password123", "Juan García", "600000000", Role.ARRENDADOR
        );

        when(userRepository.existsByUsername("juanito")).thenReturn(false);
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        UserEntity result = authService.register(request);

        assertThat(result.getUsername()).isEqualTo("juanito");
        assertThat(result.getPasswordHash()).isEqualTo("hashed");
        assertThat(result.getRole()).isEqualTo(Role.ARRENDADOR);
        assertThat(result.getIsActive()).isTrue();
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_WithExistingUsername_ShouldThrow() {
        RegisterRequest request = new RegisterRequest(
                "juanito", "otro@mail.com", "password123", null, null, Role.ARRENDADOR
        );

        when(userRepository.existsByUsername("juanito")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any());
    }

    // ------------------- login -------------------

    @Test
    void login_ShouldReturnAuthResponseWithBearerType() {
        LoginRequest request = new LoginRequest("juanito", "password123");

        UserEntity user = buildActiveUser();
        RefreshTokenEntity refreshToken = buildRefreshToken(user);

        when(userRepository.findByUsernameOrEmail("juanito")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtProvider.generateAccessToken("juanito")).thenReturn("access-token");
        when(refreshTokenRepository.save(any())).thenReturn(refreshToken);
        doNothing().when(refreshTokenRepository).deleteByUser(any());

        AuthResponse response = authService.login(request);

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(response.user().username()).isEqualTo("juanito");
    }

    @Test
    void login_WithWrongPassword_ShouldThrow() {
        LoginRequest request = new LoginRequest("juanito", "wrongpass");

        UserEntity user = buildActiveUser();
        when(userRepository.findByUsernameOrEmail("juanito")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales incorrectas");
    }

    @Test
    void login_WithInactiveUser_ShouldThrow() {
        LoginRequest request = new LoginRequest("juanito", "password123");

        UserEntity user = buildActiveUser();
        user.setIsActive(false);
        when(userRepository.findByUsernameOrEmail("juanito")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void login_WithUnknownUser_ShouldThrow() {
        LoginRequest request = new LoginRequest("fantasma", "password123");

        when(userRepository.findByUsernameOrEmail("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ------------------- refresh -------------------

    @Test
    void refresh_ShouldRevokeOldTokenAndReturnNew() {
        UserEntity user = buildActiveUser();
        RefreshTokenEntity oldToken = buildRefreshToken(user);
        RefreshTokenEntity newToken = buildRefreshToken(user);

        when(refreshTokenRepository.findByToken(oldToken.getToken())).thenReturn(Optional.of(oldToken));
        when(refreshTokenRepository.save(any())).thenReturn(newToken);
        when(jwtProvider.generateAccessToken("juanito")).thenReturn("new-access-token");
        doNothing().when(refreshTokenRepository).deleteByUser(any());

        AuthResponse response = authService.refresh(new RefreshRequest(oldToken.getToken()));

        assertThat(oldToken.getRevoked()).isTrue();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo("new-access-token");
    }

    @Test
    void refresh_WithExpiredToken_ShouldThrow() {
        UserEntity user = buildActiveUser();
        RefreshTokenEntity expiredToken = buildRefreshToken(user);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(refreshTokenRepository.findByToken(expiredToken.getToken())).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.refresh(new RefreshRequest(expiredToken.getToken())))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("expirado");
    }

    // ------------------- logout -------------------

    @Test
    void logout_ShouldRevokeToken() {
        UserEntity user = buildActiveUser();
        RefreshTokenEntity token = buildRefreshToken(user);

        when(refreshTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any())).thenReturn(token);

        authService.logout(new RefreshRequest(token.getToken()));

        assertThat(token.getRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    // ------------------- helpers -------------------

    private UserEntity buildActiveUser() {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setUsername("juanito");
        user.setEmail("juan@mail.com");
        user.setPasswordHash("hashed");
        user.setRole(Role.ARRENDADOR);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private RefreshTokenEntity buildRefreshToken(UserEntity user) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        token.setRevoked(false);
        return token;
    }
}

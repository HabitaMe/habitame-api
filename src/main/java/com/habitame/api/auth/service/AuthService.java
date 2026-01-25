package com.habitame.api.auth.service;

import com.habitame.api.auth.dto.*;
import com.habitame.api.auth.entity.RefreshTokenEntity;
import com.habitame.api.auth.repository.RefreshTokenRepository;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.exception.*;
import com.habitame.api.common.mapper.UserMapper;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final long refreshTokenDurationMinutes = 60 * 24; // 24 horas

    @Transactional
    public UserEntity register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())
                || userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Username o email ya existente");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userEntity.setFullName(request.getFullName());
        userEntity.setPhone(request.getPhone());
        userEntity.setRole(request.getRole());
        userEntity.setIsActive(true);

        return userRepository.save(userEntity);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {

        // Buscar usuario por username o email
        UserEntity userEntity = userRepository
                .findByUsernameOrEmail(request.getUsernameOrEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        // Comprobar que el usuario está activo
        if (!userEntity.getIsActive()) {
            throw new ForbiddenException("Usuario inactivo");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        // Generar tokens usando JwtProvider
        String accessToken = jwtProvider.generateAccessToken(userEntity.getUsername());
        RefreshTokenEntity refreshTokenEntity = createRefreshToken(userEntity);

        // Construir la respuesta
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenEntity.getToken());
        response.setExpiresIn(3600); // 1 hora
        response.setUser(UserMapper.toResponse(userEntity));

        return response;
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        // Buscar refresh token en BD
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token no válido"));

        // Comprobar si está revocado o expirado
        if (refreshTokenEntity.getRevoked() || refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expirado o revocado");
        }

        // Usuario asociado
        UserEntity user = refreshTokenEntity.getUser();

        // Revocar el token antiguo
        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);

        // Generar un nuevo refresh token
        RefreshTokenEntity newRefreshToken = createRefreshToken(user);

        // Generar nuevo access token
        String accessToken = jwtProvider.generateAccessToken(user.getUsername());

        // Construir respuesta
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken.getToken());
        response.setExpiresIn(3600); // 1 hora
        response.setUser(UserMapper.toResponse(user));

        return response;
    }

    public void logout(RefreshRequest request) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token no válido"));

        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        // Revocar todos los Refresh Tokens
        refreshTokenRepository.deleteByUser(user);

        RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
        tokenEntity.setUser(user);
        tokenEntity.setToken(UUID.randomUUID().toString());
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(refreshTokenDurationMinutes));
        tokenEntity.setRevoked(false);
        return refreshTokenRepository.save(tokenEntity);
    }
}

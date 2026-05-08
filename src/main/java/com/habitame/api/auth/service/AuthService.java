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
        if (userRepository.existsByUsername(request.username())
                || userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Username o email ya existente");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.username());
        userEntity.setEmail(request.email());
        userEntity.setPasswordHash(passwordEncoder.encode(request.password()));
        userEntity.setFullName(request.fullName());
        userEntity.setPhone(request.phone());
        userEntity.setRole(request.role());
        userEntity.setIsActive(true);

        return userRepository.save(userEntity);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserEntity userEntity = userRepository
                .findByUsernameOrEmail(request.usernameOrEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!userEntity.getIsActive()) {
            throw new ForbiddenException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.password(), userEntity.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        String accessToken = jwtProvider.generateAccessToken(userEntity.getUsername());
        RefreshTokenEntity refreshTokenEntity = createRefreshToken(userEntity);

        return new AuthResponse(
                accessToken,
                refreshTokenEntity.getToken(),
                "Bearer",
                3600,
                UserMapper.toResponse(userEntity)
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token no válido"));

        if (refreshTokenEntity.getRevoked() || refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expirado o revocado");
        }

        UserEntity user = refreshTokenEntity.getUser();

        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);

        RefreshTokenEntity newRefreshToken = createRefreshToken(user);
        String accessToken = jwtProvider.generateAccessToken(user.getUsername());

        return new AuthResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer",
                3600,
                UserMapper.toResponse(user)
        );
    }

    public void logout(RefreshRequest request) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token no válido"));

        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
        tokenEntity.setUser(user);
        tokenEntity.setToken(UUID.randomUUID().toString());
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(refreshTokenDurationMinutes));
        tokenEntity.setRevoked(false);
        return refreshTokenRepository.save(tokenEntity);
    }
}

package com.habitame.api.auth.repository;

import com.habitame.api.auth.entity.RefreshTokenEntity;
import com.habitame.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);
}

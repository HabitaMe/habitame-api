package com.habitame.api.user.repository;

import com.habitame.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer>,
        JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
                SELECT u FROM UserEntity u
                WHERE u.username = :value OR u.email = :value
            """)
    Optional<UserEntity> findByUsernameOrEmail(@Param("value") String value);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}


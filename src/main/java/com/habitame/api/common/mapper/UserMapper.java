package com.habitame.api.common.mapper;

import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;

public class UserMapper {

    public static UserResponse toResponse(UserEntity userEntity) {
        return new UserResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFullName(),
                userEntity.getPhone(),
                userEntity.getPhotoUrl(),
                userEntity.getEmail(),
                userEntity.getRole().name(),
                userEntity.getIsActive(),
                userEntity.getCreatedAt()
        );
    }
}

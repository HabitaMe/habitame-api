package com.habitame.api.common.mapper;

import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.UserEntity;

public class UserMapper {

    public static UserResponse toResponse(UserEntity userEntity) {
        UserResponse dto = new UserResponse();
        dto.id = userEntity.getId();
        dto.username = userEntity.getUsername();
        dto.email = userEntity.getEmail();
        dto.role = userEntity.getRole().name();
        dto.photoUrl = userEntity.getPhotoUrl();
        dto.phone = userEntity.getPhone();
        dto.fullName = userEntity.getFullName();
        dto.isActive = userEntity.getIsActive();
        dto.createdAt = userEntity.getCreatedAt();
        return dto;
    }
}

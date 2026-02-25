package com.habitame.api.common.mapper;

import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;

public class PropertyImageMapper {

    public static PropertyImageResponse toResponse(PropertyImageEntity propertyImageEntity) {
        PropertyImageResponse dto = new PropertyImageResponse();
        dto.setId(propertyImageEntity.getId());
        dto.setImageUrl(propertyImageEntity.getImageUrl());
        dto.setMain(propertyImageEntity.getIsMain());
        return dto;
    }
}

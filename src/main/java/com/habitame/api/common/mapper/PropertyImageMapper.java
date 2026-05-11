package com.habitame.api.common.mapper;

import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;

public class PropertyImageMapper {

    public static PropertyImageResponse toResponse(PropertyImageEntity propertyImageEntity) {
        return new PropertyImageResponse(
                propertyImageEntity.getId(),
                propertyImageEntity.getImageUrl(),
                propertyImageEntity.isMain()
        );
    }
}

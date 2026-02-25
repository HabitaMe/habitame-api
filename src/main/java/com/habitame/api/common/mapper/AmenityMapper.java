package com.habitame.api.common.mapper;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityEntity;

public class AmenityMapper {
    public static AmenityResponse toResponse(AmenityEntity amenityEntity) {
        AmenityResponse dto = new AmenityResponse();
        dto.id = amenityEntity.getId();
        dto.name = amenityEntity.getName();
        dto.description = amenityEntity.getDescription();
        return dto;
    }
}

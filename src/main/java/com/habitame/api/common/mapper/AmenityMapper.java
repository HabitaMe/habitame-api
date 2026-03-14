package com.habitame.api.common.mapper;

import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityEntity;

public class AmenityMapper {
    public static AmenityResponse toResponse(AmenityEntity amenityEntity) {
        AmenityResponse dto = new AmenityResponse();
        dto.setId(amenityEntity.getId());
        dto.setName(amenityEntity.getName());
        dto.setDescription(amenityEntity.getDescription());
        dto.setScope(amenityEntity.getScope().toString());
        return dto;
    }

    public static AmenityEntity toEntity(AmenityRequest request) {
        AmenityEntity amenityEntity = new AmenityEntity();
        amenityEntity.setName(request.getName());
        amenityEntity.setDescription(request.getDescription());
        amenityEntity.setScope(request.getScope());
        return amenityEntity;
    }

    public static AmenityEntity toUpdate(AmenityEntity amenityEntity, AmenityRequest request) {
        amenityEntity.setName(request.getName());
        amenityEntity.setDescription(request.getDescription());
        amenityEntity.setScope(request.getScope());
        return amenityEntity;
    }
}

package com.habitame.api.common.mapper;

import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityEntity;

public class AmenityMapper {
    public static AmenityResponse toResponse(AmenityEntity amenityEntity) {
        return new AmenityResponse(
                amenityEntity.getId(),
                amenityEntity.getName(),
                amenityEntity.getDescription(),
                amenityEntity.getScope().toString()
        );
    }

    public static AmenityEntity toEntity(AmenityRequest request) {
        return AmenityEntity.builder()
                .name(request.name())
                .description(request.description())
                .scope(request.scope())
                .build();
    }

    public static AmenityEntity toUpdate(AmenityEntity amenityEntity, AmenityRequest request) {
        amenityEntity.setName(request.name());
        amenityEntity.setDescription(request.description());
        amenityEntity.setScope(request.scope());
        return amenityEntity;
    }
}

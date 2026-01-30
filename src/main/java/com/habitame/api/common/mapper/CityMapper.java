package com.habitame.api.common.mapper;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;

public class CityMapper {
    public static CityResponse toResponse(CityEntity cityEntity) {
        CityResponse dto = new CityResponse();
        dto.setId(cityEntity.getId());
        dto.setName(cityEntity.getName());
        return dto;
    }
}

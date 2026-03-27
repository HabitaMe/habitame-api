package com.habitame.api.common.mapper;

import com.habitame.api.city.dto.CityRequest;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.province.entity.ProvinceEntity;

public class CityMapper {
    public static CityResponse toResponse(CityEntity cityEntity) {
        return new CityResponse(
                cityEntity.getId(),
                cityEntity.getName()
        );
    }

    public static CityEntity toEntity(CityRequest request, ProvinceEntity provinceEntity) {
        return CityEntity.builder()
                .name(request.name())
                .provinceEntity(provinceEntity)
                .build();
    }
}

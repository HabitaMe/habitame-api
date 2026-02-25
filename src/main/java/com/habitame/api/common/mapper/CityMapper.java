package com.habitame.api.common.mapper;

import com.habitame.api.city.dto.CityRequest;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.province.entity.ProvinceEntity;

public class CityMapper {
    public static CityResponse toResponse(CityEntity cityEntity) {
        CityResponse dto = new CityResponse();
        dto.setId(cityEntity.getId());
        dto.setName(cityEntity.getName());
        return dto;
    }

    public static CityEntity toEntity(CityRequest request, ProvinceEntity provinceEntity) {
        CityEntity cityEntity = new CityEntity();
        cityEntity.setName(request.getName());
        cityEntity.setProvinceEntity(provinceEntity);
        return cityEntity;
    }
}

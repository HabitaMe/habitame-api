package com.habitame.api.common.mapper;

import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.entity.ProvinceEntity;

public class ProvinceMapper {
    public static ProvinceResponse toResponse(ProvinceEntity provinceEntity) {;
        return new ProvinceResponse(
                provinceEntity.getId(),
                provinceEntity.getName()
        );
    }

    public static ProvinceEntity toEntity(ProvinceRequest request, CountryEntity countryEntity) {
        return ProvinceEntity.builder()
                .name(request.name())
                .countryEntity(countryEntity)
                .build();
    }
}

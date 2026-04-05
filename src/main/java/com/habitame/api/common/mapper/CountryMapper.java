package com.habitame.api.common.mapper;

import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.entity.CountryEntity;

public class CountryMapper {
    public static CountryResponse toResponse(CountryEntity countryEntity) {
        return new CountryResponse(
                countryEntity.getId(),
                countryEntity.getName(),
                countryEntity.getIsoCode()
        );
    }

    public static CountryEntity toEntity(CountryRequest request) {
        return CountryEntity.builder()
                .name(request.name())
                .isoCode(request.isoCode())
                .build();
    }
}

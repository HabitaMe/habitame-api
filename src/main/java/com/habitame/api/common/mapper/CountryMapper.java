package com.habitame.api.common.mapper;

import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.entity.CountryEntity;
import jakarta.validation.Valid;

public class CountryMapper {
    public static CountryResponse toResponse(CountryEntity countryEntity){
        CountryResponse response = new CountryResponse();
        response.setId(countryEntity.getId());
        response.setName(countryEntity.getName());
        response.setIsoCode(countryEntity.getIsoCode());
        return response;
    }

    public static CountryEntity toEntity(CountryRequest request) {
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setName(request.getName());
        countryEntity.setIsoCode(request.getIsoCode());
        return countryEntity;
    }
}

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

    public static CountryEntity toEntity(CountryRequest countryRequest) {
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setName(countryRequest.getName());
        countryEntity.setIsoCode(countryRequest.getIsoCode());
        return countryEntity;
    }
}

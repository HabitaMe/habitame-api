package com.habitame.api.common.mapper;

import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.entity.ProvinceEntity;

public class ProvinceMapper {
    public static ProvinceResponse toResponse(ProvinceEntity provinceEntity) {
        ProvinceResponse dto = new ProvinceResponse();
        dto.setId(provinceEntity.getId());
        dto.setName(provinceEntity.getName());
        return dto;
    }

    public static ProvinceEntity toEntity(ProvinceRequest provinceRequest, CountryEntity countryEntity) {
        ProvinceEntity provinceEntity = new ProvinceEntity();
        provinceEntity.setName(provinceRequest.getName());
        provinceEntity.setCountryEntity(countryEntity);
        return provinceEntity;
    }
}

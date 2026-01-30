package com.habitame.api.city.service;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.city.repository.CityRepository;
import com.habitame.api.common.mapper.CityMapper;
import com.habitame.api.province.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<CityEntity> findAll() {
        return cityRepository.findAll();
    }

    public CityEntity findById(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElse(null);
    }

    public List<CityResponse> findByProvince(Integer provinceId) {
        return cityRepository.findByProvinceEntity_Id(provinceId)
                .stream()
                .map(CityMapper::toResponse)
                .toList();
    }
}

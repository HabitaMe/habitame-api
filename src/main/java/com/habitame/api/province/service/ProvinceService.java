package com.habitame.api.province.service;

import com.habitame.api.city.repository.CityRepository;
import com.habitame.api.province.entity.ProvinceEntity;
import com.habitame.api.province.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvinceService {
    private final ProvinceRepository provinceRepository;

    public ProvinceEntity findByName(String name){
        return provinceRepository.findByName((name));
    }
}

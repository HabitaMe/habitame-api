package com.habitame.api.city.controller;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.mapper.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<CityEntity> findAll(){
        return cityService.findAll();
    }

    @GetMapping("/{id}")
    public CityEntity findById(@PathVariable Integer id){
        return cityService.findById(id);
    }

    @GetMapping("/province/{provinceId}")
    public List<CityResponse> findByProvince(@PathVariable Integer provinceId){
        return cityService.findByProvince(provinceId);
    }
}

package com.habitame.api.country.controller;

import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public List<CountryEntity> findAll(){
        return countryService.findAll();
    }

    @GetMapping("/{id}")
    public CountryEntity findById(@PathVariable Long id){
        return countryService.findById(id);
    }
}

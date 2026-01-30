package com.habitame.api.country.service;

import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<CountryEntity> findAll() {
        return countryRepository.findAll();
    }

    public CountryEntity findById(Long id) {
        return countryRepository.findById(id).orElse(null);
    }
}

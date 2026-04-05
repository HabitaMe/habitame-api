package com.habitame.api.country.service;

import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.CountryMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.repository.CountryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;

    public PageResponse<CountryResponse> findAll(Pageable pageable) {
        Page<CountryEntity> page = countryRepository.findAll(pageable);

        List<CountryResponse> content = page
                .map(CountryMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public CountryResponse findById(Integer id) {
        return CountryMapper.toResponse(findEntityById(id));
    }

    public CountryEntity findEntityById(Integer id) {
        return countryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Country not found: " + id));
    }

    @Transactional
    public CountryResponse addCountry(@Valid CountryRequest request) {
        if (countryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Country already exists: " + request.name());
        }

        CountryEntity countryEntity = CountryMapper.toEntity(request);

        CountryEntity countryEntitySaved = countryRepository.save(countryEntity);

        return CountryMapper.toResponse(countryEntitySaved);
    }

    @Transactional
    public CountryResponse updateCountry(Integer countryId, CountryRequest request) {
        CountryEntity countryEntity = findEntityById(countryId);
        countryEntity.setName(request.name());
        countryEntity.setIsoCode(request.isoCode());
        countryRepository.save(countryEntity);
        return CountryMapper.toResponse(countryEntity);
    }

    @Transactional
    public void deleteCountry(Integer countryId) {
        CountryEntity countryEntity = findEntityById(countryId);
        countryRepository.delete(countryEntity);
    }
}

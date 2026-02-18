package com.habitame.api.province.service;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.CityMapper;
import com.habitame.api.common.mapper.ProvinceMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.service.CountryService;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.entity.ProvinceEntity;
import com.habitame.api.province.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final CountryService countryService;

    public PageResponse<ProvinceResponse> findAll(Pageable pageable) {
        Page<ProvinceEntity> page = provinceRepository.findAll(pageable);

        List<ProvinceResponse> content = page
                .map(ProvinceMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public ProvinceResponse findById(Integer provinceId) {
        return ProvinceMapper.toResponse(findEntityById(provinceId));
    }

    public ProvinceEntity findEntityById(Integer provinceId) {
        return provinceRepository.findById(provinceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Province not found: " + provinceId)
                );
    }

    public PageResponse<ProvinceResponse> findByCountry (Integer countryId, Pageable pageable) {
        Page<ProvinceEntity> page = provinceRepository.findByCountryEntity_Id(countryId, pageable);

        List<ProvinceResponse> content = page
                .map(ProvinceMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public ProvinceResponse addProvince(ProvinceRequest request) {
        if (provinceRepository.existsByCountryEntity_IdAndName(
                request.getCountryId(),
                request.getName()
        )) {
            throw new DuplicateResourceException("Province already exists");
        }

        CountryEntity countryEntity = countryService.findEntityById(request.getCountryId());

        ProvinceEntity provinceEntity = ProvinceMapper.toEntity(request, countryEntity);

        ProvinceEntity savedProvinceEntity = provinceRepository.save(provinceEntity);

        return ProvinceMapper.toResponse(savedProvinceEntity);
    }

    @Transactional
    public ProvinceResponse updateProvince(Integer provinceId, ProvinceRequest request) {
        ProvinceEntity provinceEntity = findEntityById(provinceId);
        provinceEntity.setName(request.getName());
        provinceEntity.setCountryEntity(countryService.findEntityById(request.getCountryId()));
        return ProvinceMapper.toResponse(provinceRepository.save(provinceEntity));
    }

    @Transactional
    public void deleteProvince(Integer provinceId) {
        provinceRepository.delete(findEntityById(provinceId));
    }
}

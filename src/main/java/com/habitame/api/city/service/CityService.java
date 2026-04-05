package com.habitame.api.city.service;

import com.habitame.api.city.dto.CityRequest;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.city.repository.CityRepository;
import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.CityMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.province.entity.ProvinceEntity;
import com.habitame.api.province.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;
    private final ProvinceService provinceService;

    public PageResponse<CityResponse> findAll(Pageable pageable) {
        Page<CityEntity> page = cityRepository.findAll(pageable);

        List<CityResponse> content = page
                .map(CityMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public CityEntity findEntityById(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("City not found: " + cityId));
    }

    public CityResponse findById(Integer id) {
        return CityMapper.toResponse(findEntityById(id));
    }

    public PageResponse<CityResponse> findByProvince(Integer provinceId, Pageable pageable) {
        Page<CityEntity> page = cityRepository.findByProvinceEntity_Id(provinceId, pageable);

        List<CityResponse> content = page
                .map(CityMapper::toResponse)
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
    public CityResponse saveCity(CityRequest request) {
        if (cityRepository.existsByProvinceEntity_IdAndName(
                request.provinceId(),
                request.name()
        )) {
            throw new DuplicateResourceException("City already exists");
        }

        ProvinceEntity provinceEntity = provinceService.findEntityById(request.provinceId());

        CityEntity cityEntity = CityMapper.toEntity(request, provinceEntity);

        CityEntity savedCityEntity = cityRepository.save(cityEntity);

        return CityMapper.toResponse(savedCityEntity);
    }

    @Transactional
    public CityResponse updateCity(Integer id, CityRequest request) {
        CityEntity cityEntity = findEntityById(id);
        cityEntity.setName(request.name());
        if (request.provinceId() != null) {
            cityEntity.setProvinceEntity(provinceService.findEntityById(request.provinceId()));
        }
        return CityMapper.toResponse(cityRepository.save(cityEntity));
    }

    @Transactional
    public void deleteCity(Integer id) {
        cityRepository.delete(findEntityById(id));
    }
}
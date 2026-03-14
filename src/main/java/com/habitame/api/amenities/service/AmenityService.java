package com.habitame.api.amenities.service;

import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.repository.AmenityRepository;
import com.habitame.api.common.mapper.AmenityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public List<AmenityResponse> findAmenities() {
        return amenityRepository.findAll().stream().map(AmenityMapper::toResponse).toList();
    }

    public List<AmenityResponse> findAmenitiesByScope(AmenityScope scope) {
        return amenityRepository.findAllByScopeIn(List.of(scope, AmenityScope.BOTH))
                .stream()
                .map(AmenityMapper::toResponse)
                .toList();
    }

    public AmenityEntity findAmenityById(Integer amenityId) {
        return amenityRepository.findById(amenityId)
                .orElseThrow(() -> new RuntimeException("Amenity not found: " + amenityId));
    }

    @Transactional
    public AmenityResponse saveAmenity(AmenityRequest request) {
        return AmenityMapper.toResponse(amenityRepository.save(AmenityMapper.toEntity(request)));
    }

    @Transactional
    public AmenityResponse updateAmenity(Integer amenityId, AmenityRequest request) {
        AmenityEntity amenityEntity = amenityRepository.findById(amenityId).orElseThrow(() -> new RuntimeException("Amenity not found: " + amenityId));
        AmenityEntity amenityUpdated = amenityRepository.save(AmenityMapper.toUpdate(amenityEntity, request));
        return AmenityMapper.toResponse(amenityUpdated);
    }

    @Transactional
    public void deleteAmenity(Integer amenityId) {
        amenityRepository.deleteById(amenityId);
    }
}

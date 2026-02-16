package com.habitame.api.property.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.*;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.repository.PropertyListProjection;
import com.habitame.api.property.repository.PropertyRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final CityService cityService;

    public PageResponse<PropertyPublicResponse> findPublicProperties(Pageable pageable) {

        Page<PropertyEntity> page = propertyRepository.findAllByStatus(PropertyStatus.active, pageable);

        List<PropertyPublicResponse> content = page
                .map(PropertyMapper::toPublicResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PropertyPublicDetailResponse findPublicPropertyById(Integer propertyId) {
        PropertyEntity propertyFound = propertyRepository.findByIdAndStatus(propertyId, PropertyStatus.active)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        propertyFound.getPropertyAmenities().size();
        return PropertyMapper.toPublicDetailResponse(propertyFound);
    }

    public PageResponse<PropertyOwnerResponse> findAllByOwner(Pageable pageable){
        Integer ownerId = SecurityUtils.getCurrentUserId();

        Page<PropertyEntity> page = propertyRepository.findAllByOwnerId(ownerId, pageable).orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId));

        List<PropertyOwnerResponse> content = page
                .map(PropertyMapper::toOwnerResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PropertyOwnerDetailResponse findMyPropertyById(Integer idProperty) {
        Integer ownerId = SecurityUtils.getCurrentUserId();

        PropertyEntity propertyEntity = propertyRepository.findByIdAndOwnerId(ownerId, idProperty).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + idProperty));
        return PropertyMapper.toOwnerDetailResponse(propertyEntity);
    }

    public PropertyOwnerResponse addOwnerProperty(PropertyOwnerRequest propertyOwnerRequest) {
        PropertyEntity propertyEntity = PropertyMapper.ownerToEntity(propertyOwnerRequest, SecurityUtils.getCurrentUser(), cityService.findEntityById(propertyOwnerRequest.getCityId()));
        propertyRepository.save(propertyEntity);
        return PropertyMapper.toOwnerResponse(propertyEntity);
    }

    public PropertyOwnerDetailResponse updateOwnerProperty(Integer propertyId, @Valid PropertyOwnerRequest propertyOwnerRequest) {
        PropertyEntity propertyEntity = propertyRepository.findByIdAndOwnerId(SecurityUtils.getCurrentUserId(), propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        PropertyEntity propertyToUpdate = PropertyMapper.updateProperty(propertyEntity, propertyOwnerRequest, cityService.findEntityById(propertyOwnerRequest.getCityId()));
        propertyRepository.save(propertyToUpdate);
        return PropertyMapper.toOwnerDetailResponse(propertyToUpdate);
    }

    public void deleteOwnerProperty(Integer idProperty) {
        PropertyEntity propertyEntity = propertyRepository.findByIdAndOwnerId(SecurityUtils.getCurrentUserId(), idProperty).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + idProperty));
        propertyRepository.delete(propertyEntity);
    }
}

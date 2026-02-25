package com.habitame.api.property.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.*;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.repository.PropertyRepository;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final CityService cityService;
    private final PropertyReviewService propertyReviewService;
    private final UserService userService;

    public PageResponse<PropertyPublicResponse> findPublicProperties(Pageable pageable) {

        Page<PropertyEntity> page = propertyRepository.findAllByStatus(PropertyStatus.ACTIVE, pageable);

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
        PropertyEntity propertyFound = propertyRepository.findByIdAndStatus(propertyId, PropertyStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        propertyFound.getPropertyAmenities().size();
        return PropertyMapper.toPublicDetailResponse(propertyFound);
    }

    public PropertyEntity findEntityById(Integer propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
    }

    public PageResponse<PropertyOwnerResponse> findAllByOwner(Pageable pageable) {
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

    public PropertyEntity findMyPropertyEntityById(Integer idProperty) {
        Integer ownerId = SecurityUtils.getCurrentUserId();

        return propertyRepository.findByIdAndOwnerId(ownerId, idProperty).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + idProperty));
    }

    @Transactional
    public PropertyOwnerResponse addOwnerProperty(PropertyOwnerRequest request) {
        PropertyEntity propertyEntity = PropertyMapper.ownerToEntity(request, SecurityUtils.getCurrentUser(), cityService.findEntityById(request.getCityId()));
        propertyRepository.save(propertyEntity);
        propertyReviewService.addReview(propertyEntity);
        return PropertyMapper.toOwnerResponse(propertyEntity);
    }

    @Transactional
    public PropertyOwnerDetailResponse updateOwnerProperty(Integer propertyId, @Valid PropertyOwnerRequest request) {
        PropertyEntity propertyEntity = propertyRepository.findByIdAndOwnerId(SecurityUtils.getCurrentUserId(), propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        PropertyEntity propertyToUpdate = PropertyMapper.updateProperty(propertyEntity, request, cityService.findEntityById(request.getCityId()));
        if (!propertyEntity.getTitle().equals(request.getTitle()) ||
                !propertyEntity.getDescription().equals(request.getDescription()) ||
                !propertyEntity.getAddress().equals(request.getAddress())) {
            propertyToUpdate.setStatus(PropertyStatus.IN_REVIEW);
            propertyReviewService.addReview(propertyEntity);
        }
        propertyRepository.save(propertyToUpdate);
        return PropertyMapper.toOwnerDetailResponse(propertyToUpdate);
    }

    @Transactional
    public void deleteProperty(Integer idProperty) {
        PropertyEntity propertyEntity = propertyRepository.findById(idProperty).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + idProperty));
        if (propertyEntity.getOwner() != SecurityUtils.getCurrentUser() && SecurityUtils.getCurrentUser().getRole() != Role.ADMIN) {
            throw new UnauthorizedException("No tienes permiso para eliminar");
        } else {
            propertyRepository.delete(propertyEntity);
        }
    }

    public PageResponse<PropertyAdminResponse> findAll(Pageable pageable) {
        Page<PropertyEntity> page = propertyRepository.findAll(pageable);

        List<PropertyAdminResponse> content = page
                .map(PropertyMapper::toAdminResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PropertyAdminDetailResponse findById(Integer idProperty) {
        PropertyEntity propertyEntity = propertyRepository.findById(idProperty).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + idProperty));
        return PropertyMapper.toAdminDetailResponse(propertyEntity);
    }

    public PropertyAdminResponse addAdminProperty(PropertyAdminRequest request) {
        PropertyEntity propertyEntity = PropertyMapper.adminToEntity(request, userService.findById(request.getOwnerId()), cityService.findEntityById(request.getCityId()));
        return PropertyMapper.toAdminResponse(propertyRepository.save(propertyEntity));
    }

    public PropertyAdminDetailResponse updateAdminProperty(Integer idProperty, PropertyAdminRequest request) {
        PropertyEntity propertyEntity = propertyRepository.findById(idProperty).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + idProperty));
        PropertyEntity propertyToUpdate = PropertyMapper.updateAdminProperty(propertyEntity, request, cityService.findEntityById(request.getCityId()), userService.findById(request.getOwnerId()));
        propertyToUpdate.setUpdatedBy(SecurityUtils.getCurrentUser());
        propertyRepository.save(propertyToUpdate);
        return PropertyMapper.toAdminDetailResponse(propertyToUpdate);
    }
}

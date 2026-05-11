package com.habitame.api.property.service;

import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.dto.PropertyFilter;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.repository.PropertySpecification;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.repository.PropertyRepository;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import com.habitame.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final CityService cityService;
    private final PropertyReviewService propertyReviewService;
    private final UserService userService;
    private final AmenityService amenityService;
    private final PropertySecurityService propertySecurityService;

    public PageResponse<PropertyPublicResponse> findPublicProperties(PropertyFilter filter, Pageable pageable) {
        Page<PropertyEntity> page = propertyRepository.findAll(PropertySpecification.activeWith(filter), pageable);
        return toPageResponse(page, PropertyMapper::toPublicResponse);
    }

    public PropertyPublicDetailResponse findPublicPropertyById(Integer propertyId) {
        PropertyEntity property = propertyRepository.findByIdAndStatus(propertyId, PropertyStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        return PropertyMapper.toPublicDetailResponse(property);
    }

    public PageResponse<PropertyOwnerResponse> findAllByOwner(Pageable pageable) {
        Integer ownerId = SecurityUtils.getCurrentUserId();
        Page<PropertyEntity> page = propertyRepository.findAllByOwnerId(ownerId, pageable);
        return toPageResponse(page, PropertyMapper::toOwnerResponse);
    }

    public PropertyOwnerDetailResponse findMyPropertyById(Integer propertyId) {
        Integer ownerId = SecurityUtils.getCurrentUserId();
        PropertyEntity property = propertyRepository.findByIdAndOwnerId(ownerId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        return PropertyMapper.toOwnerDetailResponse(property);
    }

    @Transactional
    public PropertyOwnerResponse addOwnerProperty(PropertyOwnerRequest request) {
        PropertyEntity property = PropertyMapper.ownerToEntity(
                request,
                SecurityUtils.getCurrentUser(),
                cityService.findEntityById(request.cityId())
        );
        propertyRepository.save(property);
        propertyReviewService.addReview(property);
        return PropertyMapper.toOwnerResponse(property);
    }

    @Transactional
    public PropertyOwnerDetailResponse updateOwnerProperty(Integer propertyId, @Valid PropertyOwnerRequest request) {
        Integer ownerId = SecurityUtils.getCurrentUserId();
        PropertyEntity property = propertyRepository.findByIdAndOwnerId(ownerId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        boolean requiresReview = !property.getTitle().equals(request.title())
                || !property.getDescription().equals(request.description())
                || !property.getAddress().equals(request.address())
                || property.getStatus().equals(PropertyStatus.INACTIVE);

        PropertyMapper.updateProperty(property, request, cityService.findEntityById(request.cityId()));
        property.setUpdatedBy(SecurityUtils.getCurrentUser());

        if (requiresReview) {
            property.setStatus(PropertyStatus.IN_REVIEW);
            propertyReviewService.addReview(property);
        }

        return PropertyMapper.toOwnerDetailResponse(propertyRepository.save(property));
    }

    public PageResponse<PropertyAdminResponse> findAll(Pageable pageable) {
        Page<PropertyEntity> page = propertyRepository.findAll(pageable);
        return toPageResponse(page, PropertyMapper::toAdminResponse);
    }

    public PropertyAdminDetailResponse findById(Integer propertyId) {
        PropertyEntity property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        return PropertyMapper.toAdminDetailResponse(property);
    }

    @Transactional
    public PropertyAdminResponse saveAdminProperty(PropertyAdminRequest request) {
        PropertyEntity property = PropertyMapper.adminToEntity(
                request,
                userService.findById(request.ownerId()),
                cityService.findEntityById(request.cityId())
        );
        return PropertyMapper.toAdminResponse(propertyRepository.save(property));
    }

    @Transactional
    public PropertyAdminDetailResponse updateAdminProperty(Integer propertyId, PropertyAdminRequest request) {
        PropertyEntity property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        PropertyMapper.updateAdminProperty(
                property,
                request,
                cityService.findEntityById(request.cityId()),
                userService.findById(request.ownerId())
        );
        property.setUpdatedBy(SecurityUtils.getCurrentUser());

        return PropertyMapper.toAdminDetailResponse(propertyRepository.save(property));
    }

    @Transactional
    public PropertyReviewResponse resolveReview(Integer propertyId, PropertyReviewDecisionRequest request) {
        PropertyEntity property = findEntityById(propertyId);

        PropertyReviewResponse response = propertyReviewService.resolveReview(propertyId, request);

        property.setStatus(request.status() == PropertyReviewStatus.APPROVED ? PropertyStatus.ACTIVE : PropertyStatus.INACTIVE);

        propertyRepository.save(property);

        return response;
    }

    @Transactional
    public void deleteProperty(Integer propertyId) {
        PropertyEntity property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        propertySecurityService.checkPropertyAccess(property);
        propertyRepository.delete(property);
    }

    @Transactional
    public PropertyOwnerResponse addAmenities(Integer propertyId, List<Integer> amenityIds) {
        PropertyEntity property = findEntityById(propertyId);
        propertySecurityService.checkPropertyAccess(property);

        List<AmenityEntity> amenities = amenityIds.stream()
                .map(amenityService::findAmenityById)
                .toList();

        property.getPropertyAmenities().addAll(amenities);
        return PropertyMapper.toOwnerResponse(propertyRepository.save(property));
    }

    @Transactional
    public void removeAmenities(Integer propertyId, List<Integer> amenityIds) {
        PropertyEntity property = findEntityById(propertyId);
        propertySecurityService.checkPropertyAccess(property);

        List<AmenityEntity> amenities = amenityIds.stream()
                .map(amenityService::findAmenityById)
                .toList();

        property.getPropertyAmenities().removeAll(amenities);
        propertyRepository.save(property);
    }

    public PropertyEntity findEntityById(Integer propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
    }

    private <T> PageResponse<T> toPageResponse(Page<PropertyEntity> page, Function<PropertyEntity, T> mapper) {
        return new PageResponse<>(
                page.map(mapper).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

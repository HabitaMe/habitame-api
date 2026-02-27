package com.habitame.api.propertyReview.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.IllegalArgument;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.common.mapper.PropertyReviewMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.service.PropertySecurityService;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.propertyReview.entity.ReviewStatus;
import com.habitame.api.propertyReview.repository.PropertyReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyReviewService {
    private final PropertyReviewRepository propertyReviewRepository;
    private final PropertySecurityService propertySecurityService;

    /**
     * Crea una review pendiente para una propiedad.
     */
    @Transactional
    public void addReview(PropertyEntity propertyEntity) {
        PropertyReviewEntity propertyReviewEntity = new PropertyReviewEntity();
        propertyReviewEntity.setProperty(propertyEntity);
        propertyReviewEntity.setStatus(ReviewStatus.PENDING);
        propertyReviewRepository.save(propertyReviewEntity);
    }

    /**
     * Historial completo de reviews.
     */
    public PageResponse<PropertyReviewResponse> getReviews(Pageable pageable) {
        Page<PropertyReviewEntity> page = propertyReviewRepository.findAll(pageable);

        List<PropertyReviewResponse> content = page
                .map(PropertyReviewMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PropertyReviewDetailResponse findById(Integer id) {
        return PropertyReviewMapper.toDetailResponse(propertyReviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id)));
    }

    /**
     * Historial completo de reviews filtrado por {@link ReviewStatus}.
     */
    public PageResponse<PropertyReviewResponse> getReviewsByStatus(ReviewStatus status, Pageable pageable) {
        Page<PropertyReviewEntity> page = propertyReviewRepository.findAllByStatus(status, pageable);

        List<PropertyReviewResponse> content = page
                .map(PropertyReviewMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    /**
     * Historial completo de reviews de una propiedad — solo admins.
     */
    public List<PropertyReviewResponse> findAllByPropertyId(Integer propertyId) {
        return propertyReviewRepository.findAllByPropertyId(propertyId)
                .stream()
                .map(PropertyReviewMapper::toResponse)
                .toList();
    }

    /**
     * El admin aprueba o rechaza la review pendiente de una propiedad.
     * Actualiza el status de la review y el de la propiedad en la misma transacción.
     * Si se rechaza sin comentario se lanza excepción — el owner necesita saber qué corregir.
     *
     * @throws ResourceNotFoundException si no hay review pendiente para esa propiedad
     * @throws IllegalArgumentException  si se rechaza sin comentario
     */
    @Transactional
    public PropertyReviewResponse resolveReview(Integer propertyId, PropertyReviewDecisionRequest request) {
        if (request.getStatus() == ReviewStatus.REJECTED && (request.getComment() == null || request.getComment().isBlank())) {
            throw new IllegalArgument("Comment is required when rejecting a review");
        }

        PropertyReviewEntity review = propertyReviewRepository.findByPropertyIdAndStatus(propertyId, ReviewStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for property: " + propertyId));


        review.setStatus(request.getStatus());
        review.setComment(request.getComment());
        review.setAdmin(SecurityUtils.getCurrentUser());
        review.setReviewedAt(LocalDateTime.now());

        return PropertyReviewMapper.toResponse(propertyReviewRepository.save(review));
    }

    /**
     * Encontrar la última review rechazada de la propiedad.
     */
    public Optional<PropertyReviewDetailResponse> findLatestRejectedReview(Integer idProperty) {
        propertySecurityService.checkPropertyAccess(idProperty);
        return propertyReviewRepository.findLatestByPropertyId(idProperty)
                .filter(r -> r.getStatus() == ReviewStatus.REJECTED)
                .map(PropertyReviewMapper::toDetailResponse);
    }
}

package com.habitame.api.propertyReview.service;

import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.common.mapper.PropertyReviewMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.entity.PropertyEntity;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyReviewService {
    private final PropertyReviewRepository propertyReviewRepository;

    @Transactional
    public void addReview(PropertyEntity propertyEntity) {
        PropertyReviewEntity propertyReviewEntity = new PropertyReviewEntity();
        propertyReviewEntity.setProperty(propertyEntity);
        propertyReviewEntity.setStatus(ReviewStatus.PENDING);
        propertyReviewRepository.save(propertyReviewEntity);
    }

    @Transactional
    public void updateReview(PropertyReviewEntity propertyReviewEntity) {

    }

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
}

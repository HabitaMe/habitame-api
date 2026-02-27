package com.habitame.api.common.mapper;

import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;

public class PropertyReviewMapper {
    public static PropertyReviewResponse toResponse(PropertyReviewEntity propertyReviewEntity) {
        PropertyReviewResponse dto = new PropertyReviewResponse();
        dto.setId(propertyReviewEntity.getId());
        dto.setStatus(propertyReviewEntity.getStatus().toString());
        dto.setProperty(propertyReviewEntity.getProperty().getId());
        return dto;
    }

    public static PropertyReviewDetailResponse toDetailResponse(PropertyReviewEntity propertyReviewEntity) {
        PropertyReviewDetailResponse dto = new PropertyReviewDetailResponse();
        dto.setId(propertyReviewEntity.getId());
        dto.setStatus(propertyReviewEntity.getStatus().toString());
        dto.setComment(propertyReviewEntity.getComment());
        dto.setProperty(PropertyMapper.toOwnerResponse(propertyReviewEntity.getProperty()));
        dto.setAdmin(propertyReviewEntity.getAdmin() == null ? null : UserMapper.toResponse(propertyReviewEntity.getAdmin()));
        dto.setReviewedAt(propertyReviewEntity.getReviewedAt());
        return dto;
    }
}

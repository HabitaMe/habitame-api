package com.habitame.api.common.mapper;

import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;

public class PropertyReviewMapper {
    public static PropertyReviewResponse toResponse(PropertyReviewEntity propertyReviewEntity) {
        return new PropertyReviewResponse(
          propertyReviewEntity.getId(),
          propertyReviewEntity.getStatus().toString(),
          propertyReviewEntity.getProperty().getId()
        );
    }

    public static PropertyReviewDetailResponse toDetailResponse(PropertyReviewEntity propertyReviewEntity) {
        return new PropertyReviewDetailResponse(
                propertyReviewEntity.getId(),
                propertyReviewEntity.getStatus().toString(),
                propertyReviewEntity.getComment(),
                PropertyMapper.toOwnerResponse(propertyReviewEntity.getProperty()),
                propertyReviewEntity.getAdmin() == null ? null : UserMapper.toResponse(propertyReviewEntity.getAdmin()),
                propertyReviewEntity.getReviewedAt()
        );
    }
}

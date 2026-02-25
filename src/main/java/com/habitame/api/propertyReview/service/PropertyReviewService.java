package com.habitame.api.propertyReview.service;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.propertyReview.entity.ReviewStatus;
import com.habitame.api.propertyReview.repository.PropertyReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

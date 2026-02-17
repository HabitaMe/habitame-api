package com.habitame.api.propertyReview.service;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.propertyReview.entity.ReviewStatus;
import com.habitame.api.propertyReview.repository.PropertyReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyReviewService {
    private final PropertyReviewRepository propertyReviewRepository;

    public void addReview(PropertyEntity propertyEntity){
        PropertyReviewEntity propertyReviewEntity = new PropertyReviewEntity();
        propertyReviewEntity.setProperty(propertyEntity);
        propertyReviewEntity.setStatus(ReviewStatus.PENDING);
        propertyReviewRepository.save(propertyReviewEntity);
    }
}

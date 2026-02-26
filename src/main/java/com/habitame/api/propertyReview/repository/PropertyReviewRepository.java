package com.habitame.api.propertyReview.repository;

import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.propertyReview.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyReviewRepository extends JpaRepository<PropertyReviewEntity, Integer> {
    Page<PropertyReviewEntity> findAllByStatus(ReviewStatus status, Pageable pageable);
}

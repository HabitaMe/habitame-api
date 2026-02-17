package com.habitame.api.propertyReview.repository;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyReviewRepository extends JpaRepository<PropertyReviewEntity, Integer> {
}

package com.habitame.api.propertyReview.dto;

import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.user.dto.UserResponse;

import java.time.LocalDateTime;

public record PropertyReviewDetailResponse (
        Integer id,
        String status,
        String comment,
        PropertyOwnerResponse property,
        UserResponse admin,
        LocalDateTime reviewedAt
) { };
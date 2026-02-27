package com.habitame.api.propertyReview.dto;

import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PropertyReviewDetailResponse {
    private Integer id;

    private String status;

    private String comment;

    private PropertyOwnerResponse property;

    private UserResponse admin;

    private LocalDateTime reviewedAt;
}

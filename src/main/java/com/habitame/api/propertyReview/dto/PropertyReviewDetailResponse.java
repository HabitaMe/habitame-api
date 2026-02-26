package com.habitame.api.propertyReview.dto;

import com.habitame.api.property.dto.PropertyOwnerResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PropertyReviewDetailResponse {
    private Integer id;
    private String status;
    private String comment;
    private PropertyOwnerResponse property;
    private String admin;
    private LocalDateTime reviewedAt;
}

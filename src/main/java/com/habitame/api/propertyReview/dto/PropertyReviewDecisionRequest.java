package com.habitame.api.propertyReview.dto;

import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyReviewDecisionRequest {
    @NotNull
    private PropertyReviewStatus status;

    private String comment;
}

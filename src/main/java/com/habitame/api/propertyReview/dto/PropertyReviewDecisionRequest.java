package com.habitame.api.propertyReview.dto;

import com.habitame.api.propertyReview.entity.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PropertyReviewDecisionRequest {
    @NotNull
    private ReviewStatus status;

    private String comment;
}

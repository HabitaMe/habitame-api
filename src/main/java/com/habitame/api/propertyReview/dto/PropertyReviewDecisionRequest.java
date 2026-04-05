package com.habitame.api.propertyReview.dto;

import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import jakarta.validation.constraints.NotNull;

public record PropertyReviewDecisionRequest (
      @NotNull PropertyReviewStatus status,
      String comment
) { };
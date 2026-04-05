package com.habitame.api.roomReview.dto;

import com.habitame.api.roomReview.entity.RoomReviewStatus;
import jakarta.validation.constraints.NotNull;

public record RoomReviewDecisionRequest (
    @NotNull RoomReviewStatus status,
    String comment
) { };

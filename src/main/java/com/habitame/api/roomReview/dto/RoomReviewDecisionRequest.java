package com.habitame.api.roomReview.dto;

import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomReviewDecisionRequest {
    @NotNull
    private RoomReviewStatus status;

    private String comment;
}

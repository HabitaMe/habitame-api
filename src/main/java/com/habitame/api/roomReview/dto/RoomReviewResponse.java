package com.habitame.api.roomReview.dto;

import lombok.Data;

public record RoomReviewResponse (
    Integer id,
    String status,
    Integer room
) { };

package com.habitame.api.roomReview.dto;

public record RoomReviewResponse (
    Integer id,
    String status,
    Integer room
) { };

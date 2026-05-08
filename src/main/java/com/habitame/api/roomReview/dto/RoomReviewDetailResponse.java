package com.habitame.api.roomReview.dto;

import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.user.dto.UserResponse;

import java.time.LocalDateTime;

public record RoomReviewDetailResponse (
    Integer id,
    String status,
    String comment,
    RoomOwnerResponse room,
    UserResponse admin,
    LocalDateTime reviewedAt
) { };

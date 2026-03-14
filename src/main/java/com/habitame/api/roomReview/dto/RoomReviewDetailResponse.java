package com.habitame.api.roomReview.dto;

import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomReviewDetailResponse {
    private Integer id;
    private String status;
    private String comment;
    private RoomOwnerResponse room;
    private UserResponse admin;
    private LocalDateTime reviewedAt;
}

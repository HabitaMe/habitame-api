package com.habitame.api.common.mapper;

import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewEntity;

public class RoomReviewMapper {
    public static RoomReviewResponse toResponse(RoomReviewEntity roomReviewEntity) {
        return new RoomReviewResponse(
                roomReviewEntity.getId(),
                roomReviewEntity.getStatus().toString(),
                roomReviewEntity.getRoom().getId()
        );
    }

    public static RoomReviewDetailResponse toDetailResponse(RoomReviewEntity roomReviewEntity) {
        return new RoomReviewDetailResponse(
                roomReviewEntity.getId(),
                roomReviewEntity.getStatus().toString(),
                roomReviewEntity.getComment(),
                RoomMapper.toOwnerResponse(roomReviewEntity.getRoom()),
                roomReviewEntity.getAdmin() == null ? null : UserMapper.toResponse(roomReviewEntity.getAdmin()),
                roomReviewEntity.getReviewedAt() == null ? null : roomReviewEntity.getReviewedAt()
        );
    }
}

package com.habitame.api.common.mapper;

import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewEntity;

public class RoomReviewMapper {
    public static RoomReviewResponse toResponse(RoomReviewEntity roomReviewEntity) {
        RoomReviewResponse dto = new RoomReviewResponse();
        dto.setId(roomReviewEntity.getId());
        dto.setStatus(roomReviewEntity.getStatus().toString());
        dto.setRoom(roomReviewEntity.getRoom().getId());
        return dto;
    }

    public static RoomReviewDetailResponse toDetailResponse(RoomReviewEntity roomReviewEntity) {
        RoomReviewDetailResponse dto = new RoomReviewDetailResponse();
        dto.setId(roomReviewEntity.getId());
        dto.setStatus(roomReviewEntity.getStatus().toString());
        dto.setComment(roomReviewEntity.getComment());
        dto.setRoom(RoomMapper.toOwnerResponse(roomReviewEntity.getRoom()));
        dto.setAdmin(roomReviewEntity.getAdmin() == null ? null : UserMapper.toResponse(roomReviewEntity.getAdmin()));
        dto.setReviewedAt(roomReviewEntity.getReviewedAt());
        return dto;
    }
}

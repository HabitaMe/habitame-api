package com.habitame.api.common.mapper;

import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomImage.entity.RoomImageEntity;

public class RoomImageMapper {

    public static RoomImageResponse toResponse(RoomImageEntity roomImageEntity) {
        RoomImageResponse dto = new RoomImageResponse();
        dto.setId(roomImageEntity.getId());
        dto.setImageUrl(roomImageEntity.getImageUrl());
        dto.setMain(roomImageEntity.isMain());
        return dto;
    }

}

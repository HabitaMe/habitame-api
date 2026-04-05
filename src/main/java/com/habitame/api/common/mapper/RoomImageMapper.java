package com.habitame.api.common.mapper;

import com.habitame.api.roomImage.dto.RoomImageRequest;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomImage.entity.RoomImageEntity;

public class RoomImageMapper {

    public static RoomImageResponse toResponse(RoomImageEntity roomImageEntity) {
        return new RoomImageResponse(
                roomImageEntity.getId(),
                roomImageEntity.getImageUrl(),
                roomImageEntity.isMain()
        );
    }

}

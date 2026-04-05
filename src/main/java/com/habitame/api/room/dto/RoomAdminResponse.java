package com.habitame.api.room.dto;

import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.user.dto.UserResponse;

public record RoomAdminResponse (
    Integer id,
    String title,
    RoomImageResponse mainImage,
    String status,
    UserResponse owner
) { };
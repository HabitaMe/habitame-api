package com.habitame.api.room.dto;

import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

@Data
public class RoomAdminResponse {
    private Integer id;

    private String title;

    private RoomImageResponse mainImage;

    private String status;

    private UserResponse owner;
}

package com.habitame.api.roomImage.dto;

import lombok.Data;

@Data
public class RoomImageResponse {
    private int id;
    private String imageUrl;
    private boolean isMain;
}

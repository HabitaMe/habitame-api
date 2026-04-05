package com.habitame.api.roomImage.dto;

import lombok.Data;

public record RoomImageResponse (
    Integer id,
    String imageUrl,
    boolean isMain
) { };

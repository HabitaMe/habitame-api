package com.habitame.api.roomImage.dto;

public record RoomImageResponse (
    Integer id,
    String imageUrl,
    boolean isMain
) { };

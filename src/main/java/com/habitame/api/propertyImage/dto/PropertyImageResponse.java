package com.habitame.api.propertyImage.dto;

public record PropertyImageResponse(
        int id,
        String imageUrl,
        boolean isMain
) { };

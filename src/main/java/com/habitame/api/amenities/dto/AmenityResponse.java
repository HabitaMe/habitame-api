package com.habitame.api.amenities.dto;

public record AmenityResponse (
        Integer id,
        String name,
        String description,
        String scope
) { }
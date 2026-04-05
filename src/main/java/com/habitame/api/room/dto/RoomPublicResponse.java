package com.habitame.api.room.dto;

import lombok.Data;

import java.math.BigDecimal;

public record RoomPublicResponse (
    Integer id,
    String title,
    String city,
    BigDecimal areaM2,
    Integer maxOccupants,
    BigDecimal pricePerMonth,
    Integer floor,
    String mainImage
) { };
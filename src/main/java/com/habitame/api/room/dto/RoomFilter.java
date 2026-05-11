package com.habitame.api.room.dto;

import java.math.BigDecimal;

public record RoomFilter(
        Integer cityId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minOccupants
) {}

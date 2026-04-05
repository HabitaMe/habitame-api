package com.habitame.api.room.dto;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import lombok.Data;

import java.math.BigDecimal;

public record RoomOwnerResponse (
    Integer id,
    String title,
    BigDecimal pricePerMonth,
    BigDecimal areaM2,
    RoomImageResponse mainImage,
    String status
) { };

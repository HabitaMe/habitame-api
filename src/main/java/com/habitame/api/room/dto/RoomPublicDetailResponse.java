package com.habitame.api.room.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public record RoomPublicDetailResponse (
    Integer id,
    String title,
    String description,
    BigDecimal areaM2,
    Integer maxOccupants,
    BigDecimal pricePerMonth,
    Integer floor,
    List<RoomImageResponse> images,
    List<AmenityResponse> amenities,
    PropertyPublicResponse property
) { };

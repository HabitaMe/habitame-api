package com.habitame.api.room.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomPublicDetailResponse {
    Integer id;
    String title;
    String description;
    BigDecimal areaM2;
    Integer maxOccupants;
    BigDecimal pricePerMonth;
    Integer floor;
    List<String> images;
    List<AmenityResponse> amenities;
    PropertyPublicResponse property;
}

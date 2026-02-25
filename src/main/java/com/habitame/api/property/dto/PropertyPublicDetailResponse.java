package com.habitame.api.property.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyPublicDetailResponse {
    Integer id;
    String title;
    String description;
    String city;
    String address;
    BigDecimal areaM2;
    Integer bathrooms;
    Integer floor;
    List<String> images;
    List<AmenityResponse> amenities;
}

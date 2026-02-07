package com.habitame.api.property.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyOwnerDetailResponse {
    private Integer id;
    private String title;
    private String description;
    private String address;
    private String city;
    private BigDecimal areaM2;
    private Integer bathroomsTotal;
    private Integer floor;
    private String status;
    private List<String> images;
    private List<AmenityResponse> amenities;
}

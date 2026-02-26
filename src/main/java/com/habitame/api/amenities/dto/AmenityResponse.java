package com.habitame.api.amenities.dto;

import lombok.Data;

@Data
public class AmenityResponse {
    private Integer id;
    private String name;
    private String description;
    private String scope;
}

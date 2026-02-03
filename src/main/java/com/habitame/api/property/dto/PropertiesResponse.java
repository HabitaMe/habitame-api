package com.habitame.api.property.dto;

import lombok.Data;

@Data
public class PropertiesResponse {
    private String title;
    private String description;
    private String type;
    private String cityName;
    private String provinceName;
    private Double areaM2;
    private String mainImage;
}

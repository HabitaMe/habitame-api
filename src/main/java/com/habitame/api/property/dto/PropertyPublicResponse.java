package com.habitame.api.property.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyPublicResponse {
    private Integer id;
    private String title;
    private String city;
    private BigDecimal areaM2;
    private Integer bathroomsTotal;
    private Integer floor;
    private String mainImage;
}

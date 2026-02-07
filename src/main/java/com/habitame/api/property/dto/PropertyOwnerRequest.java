package com.habitame.api.property.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyOwnerRequest {
    private String title;
    private String description;
    private String type;
    private String address;
    private Integer cityId;
    private Integer floor;
    private BigDecimal areaM2;
    private Integer bathroomsTotal;
    private boolean ownerInHouse;
}
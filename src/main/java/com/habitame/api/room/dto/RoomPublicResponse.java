package com.habitame.api.room.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomPublicResponse {
    private Integer id;
    private String title;
    private String city;
    private BigDecimal areaM2;
    private Integer maxOccupants;
    private BigDecimal pricePerMonth;
    private Integer floor;
    private String mainImage;
}

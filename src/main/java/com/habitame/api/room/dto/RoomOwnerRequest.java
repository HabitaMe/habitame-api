package com.habitame.api.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomOwnerRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal areaM2;
    @NotNull
    private Integer maxOccupants;
    @NotNull
    private BigDecimal pricePerMonth;
    @NotNull
    private Integer floor;
    @NotNull
    private Integer propertyId;
}

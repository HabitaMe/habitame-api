package com.habitame.api.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomOwnerRequest (
    @NotBlank String title,
    @NotBlank String description,
    @NotNull BigDecimal areaM2,
    @NotNull Integer maxOccupants,
    @NotNull BigDecimal pricePerMonth,
    @NotNull Integer floor,
    @NotNull Integer propertyId
) { };

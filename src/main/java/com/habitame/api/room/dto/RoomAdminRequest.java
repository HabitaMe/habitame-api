package com.habitame.api.room.dto;

import com.habitame.api.room.entity.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomAdminRequest (
    @NotBlank String title,
    @NotBlank String description,
    @NotNull BigDecimal areaM2,
    @NotNull Integer maxOccupants,
    @NotNull BigDecimal pricePerMonth,
    @NotNull Integer floor,
    @NotNull Integer propertyId,
    @NotNull RoomStatus status,
    @NotNull Integer ownerId
) { };
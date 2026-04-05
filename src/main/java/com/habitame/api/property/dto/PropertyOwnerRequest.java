package com.habitame.api.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PropertyOwnerRequest (
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String type,
        @NotBlank String address,
        @NotNull Integer cityId,
        @NotNull Integer floor,
        @NotNull BigDecimal areaM2,
        @NotNull Integer bathroomsTotal,
        @NotNull boolean ownerInHouse
) { };
package com.habitame.api.amenities.dto;

import com.habitame.api.amenities.entity.AmenityScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AmenityRequest (
        @NotBlank String name,
        @NotBlank String description,
        @NotNull AmenityScope scope
) { }
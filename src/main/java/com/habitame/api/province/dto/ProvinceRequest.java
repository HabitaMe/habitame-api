package com.habitame.api.province.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProvinceRequest (
        @NotNull Integer countryId,
        @NotBlank String name
) { };
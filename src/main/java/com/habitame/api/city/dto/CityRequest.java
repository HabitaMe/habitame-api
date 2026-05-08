package com.habitame.api.city.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CityRequest (
      @NotBlank String name,
      @NotNull Integer provinceId
) { };
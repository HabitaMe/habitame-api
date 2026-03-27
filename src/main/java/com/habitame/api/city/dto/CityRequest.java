package com.habitame.api.city.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record CityRequest (
      @NotBlank String name,
      @NotNull Integer provinceId
) { };
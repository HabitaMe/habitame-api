package com.habitame.api.country.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public record CountryRequest (
      @NotBlank String name,
      @NotBlank String isoCode
) { };
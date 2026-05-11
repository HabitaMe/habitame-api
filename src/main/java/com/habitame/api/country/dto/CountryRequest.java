package com.habitame.api.country.dto;

import jakarta.validation.constraints.NotBlank;

public record CountryRequest (
      @NotBlank String name,
      @NotBlank String isoCode
) { };
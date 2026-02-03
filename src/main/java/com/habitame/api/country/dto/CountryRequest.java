package com.habitame.api.country.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CountryRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String isoCode;
}

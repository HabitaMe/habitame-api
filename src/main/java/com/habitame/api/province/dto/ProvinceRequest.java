package com.habitame.api.province.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProvinceRequest {
    @NotNull
    private Integer countryId;
    @NotBlank
    private String name;
}

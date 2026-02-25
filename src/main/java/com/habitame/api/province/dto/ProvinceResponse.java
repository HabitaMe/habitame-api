package com.habitame.api.province.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProvinceResponse {
    @NotNull
    private Integer id;
    @NotBlank
    private String name;
}

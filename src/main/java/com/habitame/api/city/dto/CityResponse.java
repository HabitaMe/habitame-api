package com.habitame.api.city.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CityResponse {
    String name;

    Integer id;
}

package com.habitame.api.amenities.dto;

import com.habitame.api.amenities.entity.AmenityScope;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AmenityRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private AmenityScope scope;
}

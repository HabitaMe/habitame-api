package com.habitame.api.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyAdminRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String type;
    @NotBlank
    private String address;
    @NotNull
    private Integer cityId;
    @NotNull
    private Integer floor;
    @NotNull
    private BigDecimal areaM2;
    @NotNull
    private Integer bathroomsTotal;
    @NotNull
    private boolean ownerInHouse;
    @NotNull
    private Integer ownerId;
}
package com.habitame.api.property.dto;

import java.math.BigDecimal;

public record PropertyOwnerRequest (
        String title,
        String description,
        String type,
        String address,
        Integer cityId,
        Integer floor,
        BigDecimal areaM2,
        Integer bathroomsTotal,
        boolean ownerInHouse
) { };
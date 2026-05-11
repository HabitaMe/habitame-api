package com.habitame.api.property.dto;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;

import java.math.BigDecimal;

public record PropertyOwnerResponse (
        Integer id,
        String title,
        String address,
        CityResponse city,
        BigDecimal areaM2,
        Integer bathroomsTotal,
        Integer floor,
        PropertyImageResponse mainImage,
        String status
) { };

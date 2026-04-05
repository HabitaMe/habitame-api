package com.habitame.api.property.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public record PropertyOwnerDetailResponse (
        Integer id,
        String title,
        String description,
        String address,
        CityResponse city,
        BigDecimal areaM2,
        Integer bathroomsTotal,
        Integer floor,
        String status,
        List<PropertyImageResponse> images,
        List<AmenityResponse> amenities
) { };
package com.habitame.api.property.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;

import java.math.BigDecimal;
import java.util.List;

public record PropertyPublicDetailResponse (
        Integer id,
        String title,
        String description,
        CityResponse city,
        String address,
        BigDecimal areaM2,
        Integer bathrooms,
        Integer floor,
        List<PropertyImageResponse> images,
        List<AmenityResponse> amenities
) { };

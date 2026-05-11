package com.habitame.api.property.dto;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.user.dto.UserResponse;

import java.math.BigDecimal;

public record PropertyPublicResponse (
    Integer id,
    String title,
    CityResponse city,
    BigDecimal areaM2,
    Integer bathroomsTotal,
    Integer floor,
    UserResponse owner,
    PropertyImageResponse mainImage
) { };
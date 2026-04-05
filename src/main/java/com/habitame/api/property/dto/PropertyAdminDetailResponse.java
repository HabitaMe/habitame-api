package com.habitame.api.property.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public record PropertyAdminDetailResponse (
      Integer id,
      String title,
      String description,
      String type,
      String address,
      Integer floor,
      BigDecimal areaM2,
      Integer bathroomsTotal,
      boolean ownerInHouse,
      String status,
      String createdAt,
      String updatedAt,
      UserResponse updatedBy,
      UserResponse owner,
      CityResponse city,
      List<PropertyImageResponse> images,
      List<AmenityResponse> amenities,
      List<PropertyReviewResponse> reviews
) { };
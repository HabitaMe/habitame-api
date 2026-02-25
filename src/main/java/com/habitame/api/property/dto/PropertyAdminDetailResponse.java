package com.habitame.api.property.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyAdminDetailResponse {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String address;
    private Integer floor;
    private BigDecimal areaM2;
    private Integer bathroomsTotal;
    private boolean ownerInHouse;
    private String status;
    private String createdAt;
    private String updatedAt;
    private UserResponse updatedBy;
    private UserResponse owner;
    private CityResponse city;
    private List<PropertyImageResponse> images;
    private List<AmenityResponse> amenities;
    private List<PropertyReviewResponse> reviews;
}
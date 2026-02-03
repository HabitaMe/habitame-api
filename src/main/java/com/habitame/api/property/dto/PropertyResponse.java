package com.habitame.api.property.dto;

import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

public class PropertyResponse {
    private String title;
    private String description;
    private String type;
    private String address;
    private String cityName;
    private String provinceName;
    private Integer floor;
    private Double areaM2;
    private Integer bathroomsTotal;
    private boolean ownerInHouse;
    private UserResponse owner;
    private List<PropertyImageResponse> propertyImageResponseList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

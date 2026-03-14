package com.habitame.api.room.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomAdminDetailResponse {
    private Integer id;

    private String title;

    private String description;

    private String address;

    private Integer floor;

    private BigDecimal areaM2;

    private Integer maxOccupants;

    private BigDecimal pricePerMonth;

    private String status;

    private String createdAt;

    private String updatedAt;

    private UserResponse updatedBy;

    private UserResponse owner;

    private List<RoomImageResponse> images;

    private List<AmenityResponse> amenities;

    private List<RoomReviewResponse> reviews;

    private PropertyAdminResponse property;
}
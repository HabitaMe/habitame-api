package com.habitame.api.room.dto;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.user.dto.UserResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public record RoomOwnerDetailResponse (
    Integer id,
    String title,
    String description,
    String address,
    Integer floor,
    BigDecimal areaM2,
    Integer maxOccupants,
    BigDecimal pricePerMonth,
    String status,
    UserResponse owner,
    List<RoomImageResponse> images,
    List<AmenityResponse> amenities,
    List<RoomReviewResponse> reviews,
    PropertyAdminResponse property
) { };

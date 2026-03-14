package com.habitame.api.room.dto;

import com.habitame.api.roomImage.dto.RoomImageResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomOwnerResponse {
    private Integer id;
    private String title;
    private BigDecimal pricePerMonth;
    private BigDecimal areaM2;
    private RoomImageResponse mainImage;
    private String status;
}

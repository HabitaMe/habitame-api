package com.habitame.api.common.mapper;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.entity.RoomEntity;

import java.math.BigDecimal;
import java.util.List;

public class RoomMapper {

    public static RoomPublicResponse toPublicResponse(RoomEntity roomEntity) {
        RoomPublicResponse dto = new RoomPublicResponse();
        dto.setId(roomEntity.getId());
        dto.setTitle(roomEntity.getTitle());
        dto.setCity(roomEntity.getProperty().getCityEntity().getName());
        dto.setAreaM2(roomEntity.getAreaM2());
        dto.setFloor(roomEntity.getFloor());
        dto.setMaxOccupants(roomEntity.getMaxOccupants());
        dto.setPricePerMonth(roomEntity.getPricePerMonth());
        dto.setMainImage("HABITAME"); //TODO: Get main url image
        return dto;
    }

    public static RoomPublicDetailResponse toPublicDetailResponse(RoomEntity roomEntity){
        RoomPublicDetailResponse dto = new RoomPublicDetailResponse();
        dto.setId(roomEntity.getId());
        dto.setTitle(roomEntity.getTitle());
        dto.setDescription(roomEntity.getDescription());
        dto.setAreaM2(roomEntity.getAreaM2());
        dto.setMaxOccupants(roomEntity.getMaxOccupants());
        dto.setPricePerMonth(roomEntity.getPricePerMonth());
        dto.setFloor(roomEntity.getFloor());
//        dto.setImages(); //TODO: Get images
//        dto.setAmenities(); //TODO: Get amenities
        dto.setProperty(PropertyMapper.toPublicResponse(roomEntity.getProperty()));
        return dto;
    }
}

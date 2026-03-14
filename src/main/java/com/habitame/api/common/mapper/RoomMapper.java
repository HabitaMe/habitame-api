package com.habitame.api.common.mapper;

import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.room.dto.*;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomImage.entity.RoomImageEntity;
import com.habitame.api.user.dto.UserResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
        dto.setMainImage(roomEntity.getImages().stream()
                .filter(RoomImageEntity::isMain)
                .map(RoomImageEntity::getImageUrl)
                .findFirst()
                .orElse(null));
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
        dto.setImages(roomEntity.getImages().stream().map(RoomImageMapper::toResponse).toList());
        dto.setAmenities(roomEntity.getRoomAmenities().stream().map(AmenityMapper::toResponse).toList());
        dto.setProperty(PropertyMapper.toPublicResponse(roomEntity.getProperty()));
        return dto;
    }

    public static RoomOwnerResponse toOwnerResponse(RoomEntity roomEntity) {
        RoomOwnerResponse dto = new RoomOwnerResponse();
        dto.setId(roomEntity.getId());
        dto.setTitle(roomEntity.getTitle());
        dto.setAreaM2(roomEntity.getAreaM2());
        dto.setStatus(roomEntity.getStatus().toString());
        dto.setPricePerMonth(roomEntity.getPricePerMonth());
        dto.setMainImage(roomEntity.getImages().stream()
                .filter(RoomImageEntity::isMain)
                .map(RoomImageMapper::toResponse)
                .findFirst()
                .orElse(null));
        return dto;
    }

    public static RoomOwnerDetailResponse toOwnerDetailResponse(RoomEntity room) {
        RoomOwnerDetailResponse dto = new RoomOwnerDetailResponse();
        dto.setId(room.getId());
        dto.setTitle(room.getTitle());
        dto.setDescription(room.getDescription());
        dto.setAddress(room.getProperty().getAddress());
        dto.setFloor(room.getFloor());
        dto.setAreaM2(room.getAreaM2());
        dto.setMaxOccupants(room.getMaxOccupants());
        dto.setPricePerMonth(room.getPricePerMonth());
        dto.setStatus(room.getStatus().toString());
        dto.setOwner(UserMapper.toResponse(room.getProperty().getOwner()));
        dto.setImages(room.getImages().stream().map(RoomImageMapper::toResponse).toList());
        dto.setAmenities(room.getRoomAmenities().stream().map(AmenityMapper::toResponse).toList());
        dto.setReviews(room.getReviews().stream().map(RoomReviewMapper::toResponse).toList());
        dto.setProperty(PropertyMapper.toAdminResponse(room.getProperty()));
        return dto;
    }

    public static RoomEntity ownerToEntity(RoomOwnerRequest request, PropertyEntity entityById) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setTitle(request.getTitle());
        roomEntity.setDescription(request.getDescription());
        roomEntity.setAreaM2(request.getAreaM2());
        roomEntity.setMaxOccupants(request.getMaxOccupants());
        roomEntity.setPricePerMonth(request.getPricePerMonth());
        roomEntity.setFloor(request.getFloor());
        roomEntity.setProperty(entityById);
        return roomEntity;
    }

    public static void updateOwnerRoom(RoomEntity room, RoomOwnerRequest request, PropertyEntity entityById) {
        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setAreaM2(request.getAreaM2());
        room.setMaxOccupants(request.getMaxOccupants());
        room.setPricePerMonth(request.getPricePerMonth());
        room.setFloor(request.getFloor());
        room.setProperty(entityById);
    }

    public static RoomAdminResponse toAdminResponse(RoomEntity roomEntity) {
        RoomAdminResponse dto = new RoomAdminResponse();
        dto.setId(roomEntity.getId());
        dto.setStatus(roomEntity.getStatus().toString());
        dto.setTitle(roomEntity.getTitle());
        dto.setOwner(UserMapper.toResponse(roomEntity.getProperty().getOwner()));
        dto.setMainImage(roomEntity.getImages().stream()
                .filter(RoomImageEntity::isMain)
                .map(RoomImageMapper::toResponse)
                .findFirst()
                .orElse(null));
        return dto;
    }

    public static RoomAdminDetailResponse toAdminDetailResponse(RoomEntity room) {
        RoomAdminDetailResponse dto = new RoomAdminDetailResponse();
        dto.setId(room.getId());
        dto.setTitle(room.getTitle());
        dto.setDescription(room.getDescription());
        dto.setAddress(room.getProperty().getAddress());
        dto.setFloor(room.getFloor());
        dto.setAreaM2(room.getAreaM2());
        dto.setMaxOccupants(room.getMaxOccupants());
        dto.setPricePerMonth(room.getPricePerMonth());
        dto.setStatus(room.getStatus().toString());
        dto.setCreatedAt(room.getCreatedAt().toString());
        dto.setUpdatedAt(room.getUpdatedAt() == null ? null : room.getUpdatedAt().toString());
        dto.setUpdatedBy(room.getUpdatedBy() == null ? null : UserMapper.toResponse(room.getUpdatedBy()));
        dto.setOwner(UserMapper.toResponse(room.getProperty().getOwner()));
        dto.setImages(room.getImages().stream().map(RoomImageMapper::toResponse).toList());
        dto.setAmenities(room.getRoomAmenities().stream().map(AmenityMapper::toResponse).toList());
        dto.setReviews(room.getReviews().stream().map(RoomReviewMapper::toResponse).toList());
        dto.setProperty(PropertyMapper.toAdminResponse(room.getProperty()));
        return dto;
    }

    public static RoomEntity adminToEntity(RoomAdminRequest request, PropertyEntity entityById) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setTitle(request.getTitle());
        roomEntity.setDescription(request.getDescription());
        roomEntity.setAreaM2(request.getAreaM2());
        roomEntity.setMaxOccupants(request.getMaxOccupants());
        roomEntity.setPricePerMonth(request.getPricePerMonth());
        roomEntity.setFloor(request.getFloor());
        roomEntity.setProperty(entityById);
        roomEntity.setStatus(request.getStatus());
        return roomEntity;
    }

    public static void updateAdminRoom(RoomEntity room, RoomAdminRequest request, PropertyEntity entityById) {
        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setAreaM2(request.getAreaM2());
        room.setMaxOccupants(request.getMaxOccupants());
        room.setPricePerMonth(request.getPricePerMonth());
        room.setFloor(request.getFloor());
        room.setProperty(entityById);
        room.setStatus(request.getStatus());
    }
}
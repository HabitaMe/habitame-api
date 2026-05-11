package com.habitame.api.common.mapper;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.room.dto.RoomAdminDetailResponse;
import com.habitame.api.room.dto.RoomAdminRequest;
import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.dto.RoomOwnerDetailResponse;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.roomImage.entity.RoomImageEntity;

public class RoomMapper {

    public static RoomPublicResponse toPublicResponse(RoomEntity roomEntity) {
        return new RoomPublicResponse(
                roomEntity.getId(),
                roomEntity.getTitle(),
                roomEntity.getProperty().getCityEntity().getName(),
                roomEntity.getProperty().getAddress() == null ? "" : roomEntity.getProperty().getAddress(),
                roomEntity.getAreaM2(),
                roomEntity.getMaxOccupants(),
                roomEntity.getPricePerMonth(),
                roomEntity.getFloor(),
                roomEntity.getImages().stream()
                        .filter(RoomImageEntity::isMain)
                        .map(RoomImageEntity::getImageUrl)
                        .findFirst()
                        .orElse(null)
        );
    }

    public static RoomPublicDetailResponse toPublicDetailResponse(RoomEntity roomEntity) {
        return new RoomPublicDetailResponse(
                roomEntity.getId(),
                roomEntity.getTitle(),
                roomEntity.getDescription(),
                roomEntity.getAreaM2(),
                roomEntity.getMaxOccupants(),
                roomEntity.getPricePerMonth(),
                roomEntity.getFloor(),
                roomEntity.getImages()
                        .stream()
                        .map(RoomImageMapper::toResponse)
                        .toList(),
                roomEntity.getRoomAmenities()
                        .stream()
                        .map(AmenityMapper::toResponse)
                        .toList(),
                PropertyMapper.toPublicResponse(roomEntity.getProperty())
        );
    }

    public static RoomOwnerResponse toOwnerResponse(RoomEntity roomEntity) {
        return new RoomOwnerResponse(
                roomEntity.getId(),
                roomEntity.getTitle(),
                roomEntity.getPricePerMonth(),
                roomEntity.getAreaM2(),
                roomEntity.getImages().stream()
                        .filter(RoomImageEntity::isMain)
                        .map(RoomImageMapper::toResponse)
                        .findFirst()
                        .orElse(null),
                roomEntity.getStatus().toString()
        );
    }
    public static RoomOwnerDetailResponse toOwnerDetailResponse(RoomEntity room) {return new RoomOwnerDetailResponse(
                room.getId(),
                room.getTitle(),
                room.getDescription(),
                room.getProperty().getAddress(),
                room.getFloor(),
                room.getAreaM2(),
                room.getMaxOccupants(),
                room.getPricePerMonth(),
                room.getStatus().toString(),
                UserMapper.toResponse(room.getProperty().getOwner()),
                room.getImages()
                        .stream()
                        .map(RoomImageMapper::toResponse)
                        .toList(),
                room.getRoomAmenities()
                        .stream()
                        .map(AmenityMapper::toResponse)
                        .toList(),
                room.getReviews()
                        .stream()
                        .map(RoomReviewMapper::toResponse)
                        .toList(),
                PropertyMapper.toAdminResponse(room.getProperty())
        );
    }

    public static RoomEntity ownerToEntity(RoomOwnerRequest request, PropertyEntity property) {
        return RoomEntity.builder()
                .title(request.title())
                .description(request.description())
                .areaM2(request.areaM2())
                .maxOccupants(request.maxOccupants())
                .pricePerMonth(request.pricePerMonth())
                .floor(request.floor())
                .property(property)
                .build();
    }

    public static void updateOwnerRoom(RoomEntity room, RoomOwnerRequest request, PropertyEntity entityById) {
        room.setTitle(request.title());
        room.setDescription(request.description());
        room.setAreaM2(request.areaM2());
        room.setMaxOccupants(request.maxOccupants());
        room.setPricePerMonth(request.pricePerMonth());
        room.setFloor(request.floor());
        room.setProperty(entityById);
    }

    public static RoomAdminResponse toAdminResponse(RoomEntity roomEntity) {
        return new RoomAdminResponse (
                roomEntity.getId(),
                roomEntity.getTitle(),
                roomEntity.getImages().stream()
                        .filter(RoomImageEntity::isMain)
                        .map(RoomImageMapper::toResponse)
                        .findFirst()
                        .orElse(null),
                roomEntity.getStatus().toString(),
                UserMapper.toResponse(roomEntity.getProperty().getOwner())
        );
    }

    public static RoomAdminDetailResponse toAdminDetailResponse(RoomEntity room) {
        return new RoomAdminDetailResponse(
                room.getId(),
                room.getTitle(),
                room.getDescription(),
                room.getProperty().getAddress(),
                room.getFloor(),
                room.getAreaM2(),
                room.getMaxOccupants(),
                room.getPricePerMonth(),
                room.getStatus().toString(),
                room.getCreatedAt().toString(),
                room.getUpdatedAt() == null ? null : room.getUpdatedAt().toString(),
                room.getUpdatedBy() == null ? null : UserMapper.toResponse(room.getUpdatedBy()),
                UserMapper.toResponse(room.getProperty().getOwner()),
                room.getImages()
                        .stream()
                        .map(RoomImageMapper::toResponse)
                        .toList(),
                room.getRoomAmenities()
                        .stream()
                        .map(AmenityMapper::toResponse)
                        .toList(),
                room.getReviews()
                        .stream()
                        .map(RoomReviewMapper::toResponse)
                        .toList(),
                PropertyMapper.toAdminResponse(room.getProperty())
        );
    }

    public static RoomEntity adminToEntity(RoomAdminRequest request, PropertyEntity property) {
        return RoomEntity.builder()
                .title(request.title())
                .description(request.description())
                .areaM2(request.areaM2())
                .maxOccupants(request.maxOccupants())
                .pricePerMonth(request.pricePerMonth())
                .floor(request.floor())
                .property(property)
                .status(request.status())
                .build();
    }

    public static void updateAdminRoom(RoomEntity room, RoomAdminRequest request, PropertyEntity property) {
        room.setTitle(request.title());
        room.setDescription(request.description());
        room.setAreaM2(request.areaM2());
        room.setMaxOccupants(request.maxOccupants());
        room.setPricePerMonth(request.pricePerMonth());
        room.setFloor(request.floor());
        room.setProperty(property);
        room.setStatus(request.status());
    }
}
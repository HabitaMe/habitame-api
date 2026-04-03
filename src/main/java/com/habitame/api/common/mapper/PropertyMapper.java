package com.habitame.api.common.mapper;

import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.property.dto.*;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.user.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertyMapper {

    public static PropertyPublicResponse toPublicResponse(PropertyEntity propertyEntity) {
        PropertyPublicResponse dto = new PropertyPublicResponse();
        dto.setId(propertyEntity.getId());
        dto.setTitle(propertyEntity.getTitle());
        dto.setCity(propertyEntity.getCityEntity().getName());
        dto.setAreaM2(propertyEntity.getAreaM2());
        dto.setBathroomsTotal(propertyEntity.getBathroomsTotal());
        dto.setFloor(propertyEntity.getFloor());
        dto.setMainImage(propertyEntity.getImages().stream()
                .filter(PropertyImageEntity::isMain)
                .map(PropertyImageEntity::getImageUrl)
                .findFirst()
                .orElse(null));
        return dto;
    }

    public static PropertyPublicDetailResponse toPublicDetailResponse(PropertyEntity propertyEntity) {
        PropertyPublicDetailResponse response = new PropertyPublicDetailResponse();
        response.setId(propertyEntity.getId());
        response.setTitle(propertyEntity.getTitle());
        response.setDescription(propertyEntity.getDescription());
        response.setCity(propertyEntity.getCityEntity().getName());
        response.setAddress(propertyEntity.getAddress());
        response.setAreaM2(propertyEntity.getAreaM2());
        response.setBathrooms(propertyEntity.getBathroomsTotal());
        response.setFloor(propertyEntity.getFloor());
        response.setImages(propertyEntity.getImages().stream()
                .map(PropertyImageEntity::getImageUrl)
                .toList());
        response.setAmenities(propertyEntity.getPropertyAmenities().stream()
                .map(AmenityMapper::toResponse)
                .toList());
        return response;
    }

    public static PropertyOwnerResponse toOwnerResponse(PropertyEntity propertyEntity) {
        PropertyOwnerResponse response = new PropertyOwnerResponse();
        response.setId(propertyEntity.getId());
        response.setTitle(propertyEntity.getTitle());
        response.setAddress(propertyEntity.getAddress());
        response.setCity(propertyEntity.getCityEntity().getName());
        response.setAreaM2(propertyEntity.getAreaM2());
        response.setBathroomsTotal(propertyEntity.getBathroomsTotal());
        response.setFloor(propertyEntity.getFloor());
        response.setMainImage(propertyEntity.getImages().stream()
                .filter(PropertyImageEntity::isMain)
                .map(PropertyImageEntity::getImageUrl)
                .findFirst()
                .orElse(null));
        response.setStatus(propertyEntity.getStatus().toString());
        return response;
    }

    public static PropertyOwnerDetailResponse toOwnerDetailResponse(PropertyEntity propertyEntity) {
        return new PropertyOwnerDetailResponse(
                propertyEntity.getId(),
                propertyEntity.getTitle(),
                propertyEntity.getDescription(),
                propertyEntity.getAddress(),
                CityMapper.toResponse(propertyEntity.getCityEntity()),
                propertyEntity.getAreaM2(),
                propertyEntity.getBathroomsTotal(),
                propertyEntity.getFloor(),
                propertyEntity.getStatus().toString(),
                propertyEntity.getImages().stream().map(PropertyImageMapper::toResponse).toList(),
                propertyEntity.getPropertyAmenities().stream().map(AmenityMapper::toResponse).toList()
        );
    }

    public static PropertyEntity ownerToEntity(PropertyOwnerRequest request, UserEntity owner, CityEntity cityEntity) {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setTitle(request.getTitle());
        propertyEntity.setDescription(request.getDescription());
        propertyEntity.setType(request.getType());
        propertyEntity.setAddress(request.getAddress());
        propertyEntity.setAreaM2(request.getAreaM2());
        propertyEntity.setCityEntity(cityEntity);
        propertyEntity.setBathroomsTotal(request.getBathroomsTotal());
        propertyEntity.setFloor(request.getFloor());
        propertyEntity.setOwnerInHouse(request.isOwnerInHouse());
        propertyEntity.setOwner(owner);
        return propertyEntity;
    }


    public static PropertyEntity updateProperty(PropertyEntity propertyEntity, @Valid PropertyOwnerRequest request, CityEntity entityById) {
        propertyEntity.setTitle(request.getTitle());
        propertyEntity.setDescription(request.getDescription());
        propertyEntity.setType(request.getType());
        propertyEntity.setAddress(request.getAddress());
        propertyEntity.setAreaM2(request.getAreaM2());
        propertyEntity.setBathroomsTotal(request.getBathroomsTotal());
        propertyEntity.setFloor(request.getFloor());
        propertyEntity.setOwnerInHouse(request.isOwnerInHouse());
        propertyEntity.setCityEntity(entityById);
        return propertyEntity;
    }

    public static PropertyAdminResponse toAdminResponse(PropertyEntity propertyEntity) {
        return new PropertyAdminResponse(
                propertyEntity.getId(),
                propertyEntity.getTitle(),
                propertyEntity.getImages().stream()
                        .filter(PropertyImageEntity::isMain)
                        .map(PropertyImageEntity::getImageUrl)
                        .findFirst()
                        .orElse(null),
                propertyEntity.getStatus().toString(),
                UserMapper.toResponse(propertyEntity.getOwner())
        );
    }

    public static PropertyAdminDetailResponse toAdminDetailResponse(PropertyEntity propertyEntity) {
        return new PropertyAdminDetailResponse(
                propertyEntity.getId(),
                propertyEntity.getTitle(),
                propertyEntity.getDescription(),
                propertyEntity.getType(),
                propertyEntity.getAddress(),
                propertyEntity.getFloor(),
                propertyEntity.getAreaM2(),
                propertyEntity.getBathroomsTotal(),
                propertyEntity.isOwnerInHouse(),
                propertyEntity.getStatus().toString(),
                propertyEntity.getCreatedAt().toString(),
                propertyEntity.getUpdatedAt() == null ? null : propertyEntity.getUpdatedAt().toString(),
                propertyEntity.getUpdatedBy() == null ? null : UserMapper.toResponse(propertyEntity.getUpdatedBy()),
                UserMapper.toResponse(propertyEntity.getOwner()),
                CityMapper.toResponse(propertyEntity.getCityEntity()),
                propertyEntity.getImages().stream().map(PropertyImageMapper::toResponse).toList(),
                propertyEntity.getPropertyAmenities().stream().map(AmenityMapper::toResponse).toList(),
                propertyEntity.getReviews().stream().map(PropertyReviewMapper::toResponse).toList()
        );
    }

    public static PropertyEntity adminToEntity(PropertyAdminRequest request, UserEntity owner, CityEntity cityEntity) {
        return PropertyEntity.builder()
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .address(request.address())
                .areaM2(request.areaM2())
                .bathroomsTotal(request.bathroomsTotal())
                .floor(request.floor())
                .ownerInHouse(request.ownerInHouse())
                .owner(owner)
                .cityEntity(cityEntity)
                .build();
    }

    public static PropertyEntity updateAdminProperty(PropertyEntity propertyEntity, PropertyAdminRequest request, CityEntity entityById, UserEntity owner) {
        propertyEntity.setTitle(request.title());
        propertyEntity.setDescription(request.description());
        propertyEntity.setType(request.type());
        propertyEntity.setAddress(request.address());
        propertyEntity.setAreaM2(request.areaM2());
        propertyEntity.setBathroomsTotal(request.bathroomsTotal());
        propertyEntity.setFloor(request.floor());
        propertyEntity.setOwnerInHouse(request.ownerInHouse());
        propertyEntity.setCityEntity(entityById);
        propertyEntity.setOwner(owner);
        return propertyEntity;
    }
}
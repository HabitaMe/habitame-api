package com.habitame.api.common.mapper;

import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.user.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertyMapper {

    public static PropertyPublicResponse toPublicResponse(PropertyEntity propertyEntity) {

        return new PropertyPublicResponse(
                propertyEntity.getId(),
                propertyEntity.getTitle(),
                CityMapper.toResponse(propertyEntity.getCityEntity()),
                propertyEntity.getAreaM2(),
                propertyEntity.getBathroomsTotal(),
                propertyEntity.getFloor(),
                UserMapper.toResponse(propertyEntity.getOwner()),
                propertyEntity.getImages().stream()
                        .filter(PropertyImageEntity::isMain)
                        .map(PropertyImageMapper::toResponse)
                        .findFirst()
                        .orElse(null)
        );
    }

    public static PropertyPublicDetailResponse toPublicDetailResponse(PropertyEntity propertyEntity) {
        return new PropertyPublicDetailResponse(
                propertyEntity.getId(),
                propertyEntity.getTitle(),
                propertyEntity.getDescription(),
                CityMapper.toResponse(propertyEntity.getCityEntity()),
                propertyEntity.getAddress(),
                propertyEntity.getAreaM2(),
                propertyEntity.getBathroomsTotal(),
                propertyEntity.getFloor(),
                propertyEntity.getImages().stream()
                        .map(PropertyImageMapper::toResponse)
                        .toList(),
                propertyEntity.getPropertyAmenities().stream()
                        .map(AmenityMapper::toResponse)
                        .toList()
        );
    }

    public static PropertyOwnerResponse toOwnerResponse(PropertyEntity propertyEntity) {
        return new PropertyOwnerResponse(
                propertyEntity.getId(),
                propertyEntity.getTitle(),
                propertyEntity.getAddress(),
                CityMapper.toResponse(propertyEntity.getCityEntity()),
                propertyEntity.getAreaM2(),
                propertyEntity.getBathroomsTotal(),
                propertyEntity.getFloor(),
                propertyEntity.getImages().stream()
                        .filter(PropertyImageEntity::isMain)
                        .map(PropertyImageMapper::toResponse)
                        .findFirst()
                        .orElse(null),
                propertyEntity.getStatus().toString()
        );
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
        return PropertyEntity.builder()
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .address(request.address())
                .areaM2(request.areaM2())
                .cityEntity(cityEntity)
                .bathroomsTotal(request.bathroomsTotal())
                .bathroomsTotal(request.bathroomsTotal())
                .floor(request.floor())
                .ownerInHouse(request.ownerInHouse())
                .owner(owner)
                .build();
    }


    public static PropertyEntity updateProperty(PropertyEntity propertyEntity, @Valid PropertyOwnerRequest request, CityEntity entityById) {
        propertyEntity.setTitle(request.title());
        propertyEntity.setDescription(request.description());
        propertyEntity.setType(request.type());
        propertyEntity.setAddress(request.address());
        propertyEntity.setAreaM2(request.areaM2());
        propertyEntity.setBathroomsTotal(request.bathroomsTotal());
        propertyEntity.setFloor(request.floor());
        propertyEntity.setOwnerInHouse(request.ownerInHouse());
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

    public static PropertyEntity updateAdminProperty(PropertyEntity propertyEntity, PropertyAdminRequest request, CityEntity cityEntity, UserEntity owner) {
        propertyEntity.setTitle(request.title());
        propertyEntity.setDescription(request.description());
        propertyEntity.setType(request.type());
        propertyEntity.setAddress(request.address());
        propertyEntity.setAreaM2(request.areaM2());
        propertyEntity.setBathroomsTotal(request.bathroomsTotal());
        propertyEntity.setFloor(request.floor());
        propertyEntity.setOwnerInHouse(request.ownerInHouse());
        propertyEntity.setCityEntity(cityEntity);
        propertyEntity.setOwner(owner);
        return propertyEntity;
    }
}
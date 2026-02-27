package com.habitame.api.common.mapper;

import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.property.dto.*;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.user.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
        PropertyOwnerDetailResponse response = new PropertyOwnerDetailResponse();
        response.setId(propertyEntity.getId());
        response.setTitle(propertyEntity.getTitle());
        response.setDescription(propertyEntity.getDescription());
        response.setAddress(propertyEntity.getAddress());
        response.setCity(propertyEntity.getCityEntity().getName());
        response.setAreaM2(propertyEntity.getAreaM2());
        response.setBathroomsTotal(propertyEntity.getBathroomsTotal());
        response.setFloor(propertyEntity.getFloor());
        response.setStatus(propertyEntity.getStatus().toString());
        response.setImages(propertyEntity.getImages().stream().map(PropertyImageEntity::getImageUrl).toList());
        response.setAmenities(propertyEntity.getPropertyAmenities().stream().map(AmenityMapper::toResponse).toList());
        return response;
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
        PropertyAdminResponse response = new PropertyAdminResponse();
        response.setId(propertyEntity.getId());
        response.setTitle(propertyEntity.getTitle());
        response.setOwner(UserMapper.toResponse(propertyEntity.getOwner()));
        response.setMainImage(propertyEntity.getImages().stream()
                .filter(PropertyImageEntity::isMain)
                .map(PropertyImageEntity::getImageUrl)
                .findFirst()
                .orElse(null));
        response.setStatus(propertyEntity.getStatus().toString());
        return response;
    }

    public static PropertyAdminDetailResponse toAdminDetailResponse(PropertyEntity propertyEntity) {
        PropertyAdminDetailResponse response = new PropertyAdminDetailResponse();
        response.setId(propertyEntity.getId());
        response.setTitle(propertyEntity.getTitle());
        response.setDescription(propertyEntity.getDescription());
        response.setType(propertyEntity.getType());
        response.setAddress(propertyEntity.getAddress());
        response.setFloor(propertyEntity.getFloor());
        response.setAreaM2(propertyEntity.getAreaM2());
        response.setBathroomsTotal(propertyEntity.getBathroomsTotal());
        response.setOwnerInHouse(propertyEntity.isOwnerInHouse());
        response.setStatus(propertyEntity.getStatus().toString());
        response.setCreatedAt(propertyEntity.getCreatedAt().toString());
        response.setUpdatedAt(propertyEntity.getUpdatedAt() == null ? null : propertyEntity.getUpdatedAt().toString());
        response.setUpdatedBy(propertyEntity.getUpdatedBy() == null ? null : UserMapper.toResponse(propertyEntity.getUpdatedBy()));
        response.setOwner(UserMapper.toResponse(propertyEntity.getOwner()));
        response.setCity(CityMapper.toResponse(propertyEntity.getCityEntity()));
        response.setImages(propertyEntity.getImages().stream().map(PropertyImageMapper::toResponse).toList());
        response.setAmenities(propertyEntity.getPropertyAmenities().stream().map(AmenityMapper::toResponse).toList());
        response.setReviews(propertyEntity.getReviews().stream().map(PropertyReviewMapper::toResponse).toList());
        return response;
    }

    public static PropertyEntity adminToEntity(PropertyAdminRequest request, UserEntity owner, CityEntity cityEntity) {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setTitle(request.getTitle());
        propertyEntity.setDescription(request.getDescription());
        propertyEntity.setType(request.getType());
        propertyEntity.setAddress(request.getAddress());
        propertyEntity.setAreaM2(request.getAreaM2());
        propertyEntity.setBathroomsTotal(request.getBathroomsTotal());
        propertyEntity.setFloor(request.getFloor());
        propertyEntity.setOwnerInHouse(request.isOwnerInHouse());
        propertyEntity.setOwner(owner);
        propertyEntity.setCityEntity(cityEntity);
        return propertyEntity;
    }

    public static PropertyEntity updateAdminProperty(PropertyEntity propertyEntity, PropertyAdminRequest request, CityEntity entityById, UserEntity owner) {
        propertyEntity.setTitle(request.getTitle());
        propertyEntity.setDescription(request.getDescription());
        propertyEntity.setType(request.getType());
        propertyEntity.setAddress(request.getAddress());
        propertyEntity.setAreaM2(request.getAreaM2());
        propertyEntity.setBathroomsTotal(request.getBathroomsTotal());
        propertyEntity.setFloor(request.getFloor());
        propertyEntity.setOwnerInHouse(request.isOwnerInHouse());
        propertyEntity.setCityEntity(entityById);
        propertyEntity.setOwner(owner);
        return propertyEntity;
    }
}
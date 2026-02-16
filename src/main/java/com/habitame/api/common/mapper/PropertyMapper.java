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
                    .filter(PropertyImageEntity::getIsMain)
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
                .filter(PropertyImageEntity::getIsMain)
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

    public static PropertyEntity ownerToEntity(PropertyOwnerRequest propertyOwnerRequest, UserEntity owner, CityEntity cityEntity) {
        PropertyEntity propertyEntity = new PropertyEntity();
        propertyEntity.setTitle(propertyOwnerRequest.getTitle());
        propertyEntity.setDescription(propertyOwnerRequest.getDescription());
        propertyEntity.setType(propertyOwnerRequest.getType());
        propertyEntity.setAddress(propertyOwnerRequest.getAddress());
        propertyEntity.setAreaM2(propertyOwnerRequest.getAreaM2());
        propertyEntity.setCityEntity(cityEntity);
        propertyEntity.setBathroomsTotal(propertyOwnerRequest.getBathroomsTotal());
        propertyEntity.setFloor(propertyOwnerRequest.getFloor());
        propertyEntity.setOwnerInHouse(propertyOwnerRequest.isOwnerInHouse());
        propertyEntity.setOwner(owner);
        return propertyEntity;
    }

    public static PropertyEntity updateProperty(PropertyEntity propertyEntity, @Valid PropertyOwnerRequest propertyOwnerRequest, CityEntity entityById) {
        propertyEntity.setTitle(propertyOwnerRequest.getTitle());
        propertyEntity.setDescription(propertyOwnerRequest.getDescription());
        propertyEntity.setType(propertyOwnerRequest.getType());
        propertyEntity.setAddress(propertyOwnerRequest.getAddress());
        propertyEntity.setAreaM2(propertyOwnerRequest.getAreaM2());
        propertyEntity.setBathroomsTotal(propertyOwnerRequest.getBathroomsTotal());
        propertyEntity.setFloor(propertyOwnerRequest.getFloor());
        propertyEntity.setOwnerInHouse(propertyOwnerRequest.isOwnerInHouse());
        propertyEntity.setCityEntity(entityById);
        return propertyEntity;
    }
}
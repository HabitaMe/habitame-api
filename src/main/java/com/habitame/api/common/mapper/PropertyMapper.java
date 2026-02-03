package com.habitame.api.common.mapper;

import com.habitame.api.city.service.CityService;
import com.habitame.api.property.dto.PropertiesResponse;
import com.habitame.api.property.dto.PropertyRequest;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertyMapper {

    public static PropertyEntity toEntity(PropertyRequest propertyRequest) {
        PropertyEntity propertyEntity = null;
        return propertyEntity;
    }

    public static PropertiesResponse toListResponse(PropertyEntity propertyEntity) {
        PropertiesResponse propertiesResponse = new PropertiesResponse();
        propertiesResponse.setTitle(propertyEntity.getTitle());
        propertiesResponse.setDescription(propertyEntity.getDescription());
        propertiesResponse.setType(propertyEntity.getType());
        propertiesResponse.setCityName(propertyEntity.getCityEntity().getName());
        propertiesResponse.setProvinceName(propertyEntity.getCityEntity().getProvinceEntity().getName());
        propertiesResponse.setAreaM2(propertyEntity.getAreaM2());
        propertiesResponse.setMainImage(propertyEntity.getImages().stream()
                .filter(PropertyImageEntity::getIsMain)
                .map(PropertyImageEntity::getImageUrl)
                .findFirst()
                .orElse(null));
        return propertiesResponse;
    }
}
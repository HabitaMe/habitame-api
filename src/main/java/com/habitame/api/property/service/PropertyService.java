package com.habitame.api.property.service;

import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.property.dto.PropertiesResponse;
import com.habitame.api.property.dto.PropertyRequest;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    public List<PropertiesResponse> getAllProperties() {
        return propertyRepository.findAll().stream().map(PropertyMapper::toListResponse).toList();
    }

    public void createProperty(PropertyRequest propertyRequest) {
        PropertyEntity propertyEntity = PropertyMapper.toEntity(propertyRequest);
        propertyRepository.save(propertyEntity);
    }
}

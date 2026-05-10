package com.habitame.api.property.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.repository.PropertyRepository;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertySecurityService {

    private final PropertyRepository propertyRepository;

    public void checkPropertyAccess(Integer propertyId) {
        PropertyEntity property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        checkPropertyAccess(property);
    }

    public void checkPropertyAccess(PropertyEntity property) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isOwnerOf(property.getOwner())) {
            throw new ForbiddenException("Don't have permission to access this property");
        }
    }

    public void checkImageAccess(PropertyImageEntity image) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isOwnerOf(image.getProperty().getOwner())) {
            throw new ForbiddenException("Don't have permission to access this image");
        }
    }
}

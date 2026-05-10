package com.habitame.api.propertyImage.service;

import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.PropertyImageMapper;
import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertySecurityService;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.propertyImage.repository.PropertyImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final ImageStorageService imageStorageService;
    private final PropertyService propertyService;
    private final PropertySecurityService propertySecurityService;

    @Transactional
    public PropertyImageResponse upload(Integer propertyId, PropertyImageRequest request) throws IOException {
        PropertyEntity property = propertyService.findEntityById(propertyId);

        propertySecurityService.checkPropertyAccess(property);

        if (request.file().isEmpty() || !isImage(request.file())) {
            throw new IllegalArgumentException("Archivo inválido: debe ser una imagen no vacía");
        }

        // Si no hay imagen principal, esta pasa a serlo automáticamente
        boolean main = request.isMain() || propertyImageRepository.countMainImages(propertyId) == 0;

        if (main) {
            propertyImageRepository.resetMainImage(propertyId);
        }

        String url = imageStorageService.store(request.file(), "properties");

        PropertyImageEntity propertyImageEntity = PropertyImageEntity.builder()
                .property(property)
                .imageUrl(url)
                .isMain(main)
                .build();

        return PropertyImageMapper.toResponse(propertyImageRepository.save(propertyImageEntity));
    }

    @Transactional
    public void delete(Integer imageId) throws IOException {
        PropertyImageEntity image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada: " + imageId));

        propertySecurityService.checkImageAccess(image);

        imageStorageService.delete(image.getImageUrl());

        propertyImageRepository.deleteById(imageId);
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public List<PropertyImageResponse> findByPropertyId(Integer propertyId) {
        return propertyImageRepository.findAllByPropertyId(propertyId)
                .stream()
                .map(PropertyImageMapper::toResponse)
                .toList();
    }
}
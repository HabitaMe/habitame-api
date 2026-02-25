package com.habitame.api.propertyImage.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.IllegalArgument;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.common.mapper.PropertyImageMapper;
import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.propertyImage.repository.PropertyImageRepository;
import com.habitame.api.user.entity.Role;
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

    @Transactional
    public PropertyImageResponse upload(Integer propertyId, PropertyImageRequest request) throws IOException {
        PropertyEntity propertyEntity = propertyService.findEntityById(propertyId);

        Integer currentUserId = SecurityUtils.getCurrentUserId();
        Role currentUserRole = SecurityUtils.getCurrentUser().getRole();
        if (!propertyEntity.getOwner().getId().equals(currentUserId) && !currentUserRole.equals(Role.ADMIN)) {
            throw new UnauthorizedException("No tienes permiso para añadir una imagen a esta propiedad");
        }

        if (request.getFile().isEmpty() || !isImage(request.getFile())) {
            throw new IllegalArgument("Archivo invalido: debe ser una imagen no vacia");
        }

        if (!request.isMain() && propertyImageRepository.countMainImages(propertyId) == 0) {
            request.setMain(true);
        }

        if (request.isMain()) {
            propertyImageRepository.resetMainImage(propertyId);
        }

        String url = imageStorageService.store(request.getFile(), "properties");

        PropertyImageEntity propertyImageEntity = PropertyImageEntity.builder()
                .property(propertyEntity)
                .imageUrl(url)
                .isMain(request.isMain())
                .build();

        PropertyImageEntity saved = propertyImageRepository.save(propertyImageEntity);

        return PropertyImageMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Integer imageId) throws IOException {
        PropertyImageEntity propertyImageEntity = propertyImageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada: " + imageId));

        Integer currentUserId = SecurityUtils.getCurrentUserId();
        Role currentUserRole = SecurityUtils.getCurrentUser().getRole();
        if (!propertyImageEntity.getProperty().getOwner().getId().equals(currentUserId) && !currentUserRole.equals(Role.ADMIN)) {
            throw new UnauthorizedException("No tienes permiso para eliminar");
        }

        try {
            imageStorageService.delete(propertyImageEntity.getImageUrl());
        } catch (IOException e) {
            throw new IOException("Error al eliminar la imagen");
        }
        propertyImageRepository.deleteById(imageId);
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public List<PropertyImageResponse> findByPropertyId(Integer idProperty) {
        return propertyImageRepository.findAllByPropertyId(idProperty)
                .stream()
                .map(PropertyImageMapper::toResponse)
                .toList();
    }
}

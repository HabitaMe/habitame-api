package com.habitame.api.property.controller;

import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.service.PropertyImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractPropertyController {

    protected final PropertyImageService propertyImageService;
    protected final PropertyService propertyService;

    @GetMapping("/{propertyId}/images")
    public ResponseEntity<List<PropertyImageResponse>> findImages(@PathVariable Integer propertyId) {
        return ResponseEntity.ok(propertyImageService.findByPropertyId(propertyId));
    }

    @PostMapping("/{propertyId}/images")
    public ResponseEntity<PropertyImageResponse> addImage(
            @PathVariable Integer propertyId,
            @Valid PropertyImageRequest request) throws IOException {
        return ResponseEntity.ok(propertyImageService.upload(propertyId, request));
    }

    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Integer id) throws IOException {
        propertyImageService.delete(id);
    }

    @PostMapping("/{propertyId}/amenities")
    public ResponseEntity<PropertyOwnerResponse> addAmenities(
            @PathVariable Integer propertyId,
            @RequestBody List<Integer> amenities) {
        return ResponseEntity.ok(propertyService.addAmenities(propertyId, amenities));
    }

    @DeleteMapping("/{propertyId}/amenities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenities(
            @PathVariable Integer propertyId,
            @RequestBody List<Integer> amenities) {
        propertyService.removeAmenities(propertyId, amenities);
    }
}
package com.habitame.api.property.controller;

import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.service.PropertyImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractPropertyController {

    private final PropertyImageService propertyImageService;
    private final PropertyService propertyService;

    @GetMapping("/{idProperty}/images")
    public ResponseEntity<List<PropertyImageResponse>> findImages(@PathVariable Integer idProperty) {
        return ResponseEntity.ok(propertyImageService.findByPropertyId(idProperty));
    }

    @PostMapping("/{idProperty}/images")
    public ResponseEntity<PropertyImageResponse> addImage(
            @PathVariable Integer idProperty,
            PropertyImageRequest request) throws IOException {
        return ResponseEntity.ok(propertyImageService.upload(idProperty, request));
    }

    @DeleteMapping("/images/{idImage}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Integer idImage) throws IOException {
        propertyImageService.delete(idImage);
    }

    @PostMapping("/{idProperty}/amenities")
    public ResponseEntity<PropertyOwnerResponse> addAmenities(
            @PathVariable Integer idProperty,
            @RequestBody List<Integer> amenities) {
        return ResponseEntity.ok(propertyService.addAmenities(idProperty, amenities));
    }

    @DeleteMapping("/{idProperty}/amenities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenities(
            @PathVariable Integer idProperty,
            @RequestBody List<Integer> amenities) {
        propertyService.removeAmenities(idProperty, amenities);
    }
}
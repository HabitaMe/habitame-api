package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.service.PropertyImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/owner/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ARRENDADOR')")
public class OwnerPropertyController {

    private final PropertyService propertyService;
    private final PropertyImageService propertyImageService;

    @GetMapping
    public ResponseEntity<PageResponse<PropertyOwnerResponse>> findMyProperties(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findAllByOwner(pageable));
    }

    @GetMapping("/{idProperty}")
    public ResponseEntity<PropertyOwnerDetailResponse> findMyPropertyById(@PathVariable Integer idProperty) {
        return ResponseEntity.ok(propertyService.findMyPropertyById(idProperty));
    }

    @PostMapping
    public ResponseEntity<Void> addOwnerProperty(@RequestBody @Valid PropertyOwnerRequest request) {
        PropertyOwnerResponse propertyOwnerResponse = propertyService.addOwnerProperty(request);
        URI location = URI.create("api/owner/properties/" + propertyOwnerResponse.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idProperty}")
    public ResponseEntity<PropertyOwnerDetailResponse> updateOwnerProperty(@PathVariable Integer idProperty, @RequestBody @Valid PropertyOwnerRequest request) {
        return ResponseEntity.ok(propertyService.updateOwnerProperty(idProperty, request));
    }

    @DeleteMapping("/{idProperty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnerProperty(@PathVariable Integer idProperty) {
        propertyService.deleteProperty(idProperty);
    }

    @GetMapping("/{idProperty}/images")
    public ResponseEntity<List<PropertyImageResponse>> findMyPropertyImages(@PathVariable Integer idProperty) {
        return ResponseEntity.ok(propertyImageService.findByPropertyId(idProperty));
    }

    @PostMapping("/{idProperty}/images")
    public ResponseEntity<PropertyImageResponse> addPropertyImage(@PathVariable Integer idProperty, PropertyImageRequest request) throws IOException {
        return ResponseEntity.ok(propertyImageService.upload(idProperty, request));
    }

    @DeleteMapping("images/{idImage}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePropertyImage(@PathVariable Integer idImage) throws IOException {
        propertyImageService.delete(idImage);
    }

    @PostMapping("/{idProperty}/amenities")
    public ResponseEntity<PropertyOwnerResponse> addAmenities(@PathVariable Integer idProperty, @RequestBody List<Integer> amenities) {
        return ResponseEntity.ok(propertyService.addAmenities(idProperty, amenities));
    }

    @PostMapping("/{idProperty}/amenities/delete")
    public ResponseEntity<PropertyOwnerResponse> removeAmenities(@PathVariable Integer idProperty, @RequestBody List<Integer> amenities) {
        return ResponseEntity.ok(propertyService.removeAmenities(idProperty, amenities));
    }
}

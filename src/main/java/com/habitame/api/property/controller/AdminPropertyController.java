package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.service.PropertyImageService;
import com.habitame.api.propertyReview.service.PropertyReviewService;
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
@RequestMapping("/api/admin/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPropertyController {

    private final PropertyService propertyService;
    private final PropertyReviewService propertyReviewService;
    private final PropertyImageService propertyImageService;

    @GetMapping
    public ResponseEntity<PageResponse<PropertyAdminResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findAll(pageable));
    }

    @GetMapping("/{idProperty}")
    public ResponseEntity<PropertyAdminDetailResponse> findById(@PathVariable Integer idProperty) {
        return ResponseEntity.ok(propertyService.findById(idProperty));
    }

    @PostMapping
    public ResponseEntity<Void> addAdminProperty(@RequestBody PropertyAdminRequest request) {
        PropertyAdminResponse response = propertyService.addAdminProperty(request);
        URI location = URI.create("api/admin/properties/" + response.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idProperty}")
    public ResponseEntity<PropertyAdminDetailResponse> updateAdminProperty(@PathVariable Integer idProperty, @RequestBody @Valid PropertyAdminRequest request) {
        return ResponseEntity.ok(propertyService.updateAdminProperty(idProperty, request));
    }

    @DeleteMapping("/{idProperty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminProperty(@PathVariable Integer idProperty) {
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
}

package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/properties")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPropertyController extends AbstractPropertyController {

    private final PropertyService propertyService;

    public AdminPropertyController(PropertyImageService propertyImageService,
                                   PropertyService propertyService) {
        super(propertyImageService, propertyService);
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PropertyAdminResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findAll(pageable));
    }

    @GetMapping("/{idProperty}")
    public ResponseEntity<PropertyAdminDetailResponse> findById(@PathVariable Integer idProperty) {
        return ResponseEntity.ok(propertyService.findById(idProperty));
    }

    @PostMapping
    public ResponseEntity<Void> saveProperty(@RequestBody @Valid PropertyAdminRequest request) {
        PropertyAdminResponse response = propertyService.saveAdminProperty(request);
        URI location = URI.create("api/admin/properties/" + response.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idProperty}")
    public ResponseEntity<PropertyAdminDetailResponse> updateAdminProperty(
            @PathVariable Integer idProperty,
            @RequestBody @Valid PropertyAdminRequest request) {
        return ResponseEntity.ok(propertyService.updateAdminProperty(idProperty, request));
    }

    @DeleteMapping("/{idProperty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminProperty(@PathVariable Integer idProperty) {
        propertyService.deleteProperty(idProperty);
    }
}
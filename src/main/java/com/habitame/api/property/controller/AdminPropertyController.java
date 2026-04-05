package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/properties")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPropertyController extends AbstractPropertyController {

    private final PropertyReviewService propertyReviewService;

    public AdminPropertyController(PropertyImageService propertyImageService,
                                   PropertyService propertyService, PropertyReviewService propertyReviewService) {
        super(propertyImageService, propertyService);
        this.propertyReviewService = propertyReviewService;
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
        URI location = URI.create("api/admin/properties/" + response.id());
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

    @GetMapping("/{idProperty}/reviews")
    public ResponseEntity<List<PropertyReviewResponse>> findReviews(@PathVariable Integer idProperty) {
        return ResponseEntity.ok(propertyReviewService.findAllByPropertyId(idProperty));
    }

    @PatchMapping("{idProperty}/reviews/resolve")
    public ResponseEntity<PropertyReviewResponse> resolveReview(@PathVariable Integer idProperty, @RequestBody @Valid PropertyReviewDecisionRequest request) {
        return ResponseEntity.ok(propertyService.resolveReview(idProperty, request));
    }
}
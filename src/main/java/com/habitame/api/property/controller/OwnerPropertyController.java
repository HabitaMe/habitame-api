package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/owner/properties")
@PreAuthorize("hasRole('ARRENDADOR')")
public class OwnerPropertyController extends AbstractPropertyController {

    private final PropertyReviewService propertyReviewService;

    public OwnerPropertyController(PropertyImageService propertyImageService,
                                   PropertyService propertyService, PropertyReviewService propertyReviewService) {
        super(propertyImageService, propertyService);
        this.propertyReviewService = propertyReviewService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PropertyOwnerResponse>> findMyProperties(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findAllByOwner(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyOwnerDetailResponse> findMyPropertyById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyService.findMyPropertyById(id));
    }

    @PostMapping
    public ResponseEntity<Void> addOwnerProperty(@RequestBody @Valid PropertyOwnerRequest request) {
        PropertyOwnerResponse response = propertyService.addOwnerProperty(request);
        URI location = URI.create("api/owner/properties/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyOwnerDetailResponse> updateOwnerProperty(
            @PathVariable Integer id,
            @RequestBody @Valid PropertyOwnerRequest request) {
        return ResponseEntity.ok(propertyService.updateOwnerProperty(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnerProperty(@PathVariable Integer id) {
        propertyService.deleteProperty(id);
    }

    @GetMapping("/{id}/reviews/latest")
    public ResponseEntity<PropertyReviewDetailResponse> findLatestRejectedReview(@PathVariable Integer id) {
        return propertyReviewService.findLatestRejectedReview(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
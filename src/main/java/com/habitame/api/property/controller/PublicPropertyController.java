package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/properties")
@RequiredArgsConstructor
public class PublicPropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<PageResponse<PropertyPublicResponse>> getPropertyList(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findPublicProperties(pageable));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyPublicDetailResponse> findById(@PathVariable Integer propertyId) {
        return ResponseEntity.ok(propertyService.findPublicPropertyById(propertyId));
    }
}
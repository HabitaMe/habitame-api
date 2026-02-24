package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyReview.repository.PropertyReviewRepository;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPropertyController {

    private final PropertyService  propertyService;
    private final PropertyReviewService propertyReviewService;

    @GetMapping
    public ResponseEntity<PageResponse<PropertyAdminResponse>> findAll(Pageable pageable){
        return ResponseEntity.ok(propertyService.findAll(pageable));
    }

    @GetMapping("/{idProperty}")
    public ResponseEntity<PropertyAdminDetailResponse> findById(@PathVariable Integer idProperty){
        return ResponseEntity.ok(propertyService.findById(idProperty));
    }
}

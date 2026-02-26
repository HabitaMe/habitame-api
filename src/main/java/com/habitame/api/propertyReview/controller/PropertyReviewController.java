package com.habitame.api.propertyReview.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.ReviewStatus;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/property-reviews")
@RequiredArgsConstructor
public class PropertyReviewController {

    private final PropertyReviewService propertyReviewService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<PropertyReviewResponse>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(propertyReviewService.getReviews(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<PropertyReviewResponse>> getReviews(@PathVariable ReviewStatus status, Pageable pageable) {
        return ResponseEntity.ok(propertyReviewService.getReviewsByStatus(status, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PropertyReviewDetailResponse> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyReviewService.findById(id));
    }
}

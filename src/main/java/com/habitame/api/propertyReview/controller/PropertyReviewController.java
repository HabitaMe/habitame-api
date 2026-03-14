package com.habitame.api.propertyReview.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
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
@RequestMapping("/api/property-reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PropertyReviewController {

    private final PropertyReviewService propertyReviewService;

    @GetMapping
    public ResponseEntity<PageResponse<PropertyReviewResponse>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(propertyReviewService.getReviews(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<PropertyReviewResponse>> getReviews(@PathVariable PropertyReviewStatus status, Pageable pageable) {
        return ResponseEntity.ok(propertyReviewService.getReviewsByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyReviewDetailResponse> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyReviewService.findById(id));
    }
}

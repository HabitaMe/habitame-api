package com.habitame.api.roomReview.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import com.habitame.api.roomReview.service.RoomReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/room-reviews")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoomReviewController {

    private final RoomReviewService roomReviewService;

    @GetMapping
    public ResponseEntity<PageResponse<RoomReviewResponse>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(roomReviewService.getReviews(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<RoomReviewResponse>> getReviews(@PathVariable RoomReviewStatus status, Pageable pageable) {
        return ResponseEntity.ok(roomReviewService.getReviewsByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomReviewDetailResponse> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomReviewService.findById(id));
    }

}

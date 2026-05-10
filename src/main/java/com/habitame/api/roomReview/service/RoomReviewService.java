package com.habitame.api.roomReview.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.IllegalArgument;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.RoomReviewMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.service.RoomSecurityService;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewEntity;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import com.habitame.api.roomReview.repository.RoomReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomReviewService {

    private final RoomReviewRepository roomReviewRepository;
    private final RoomSecurityService roomSecurityService;

    @Transactional
    public void addReview(RoomEntity roomEntity) {
        RoomReviewEntity roomReviewEntity = new RoomReviewEntity();
        roomReviewEntity.setRoom(roomEntity);
        roomReviewEntity.setStatus(RoomReviewStatus.PENDING);
        roomReviewRepository.save(roomReviewEntity);
    }

    public PageResponse<RoomReviewResponse> getReviews(Pageable pageable) {
        Page<RoomReviewEntity> page = roomReviewRepository.findAll(pageable);

        List<RoomReviewResponse> content = page
                .map(RoomReviewMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public RoomReviewDetailResponse findById(Integer id) {
        return RoomReviewMapper.toDetailResponse(roomReviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id)));
    }

    public PageResponse<RoomReviewResponse> getReviewsByStatus(RoomReviewStatus status, Pageable pageable) {
        Page<RoomReviewEntity> page = roomReviewRepository.findAllByStatus(status, pageable);

        List<RoomReviewResponse> content = page
                .map(RoomReviewMapper::toResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public List<RoomReviewResponse> findAllByRoomId(Integer roomId) {
        return roomReviewRepository.findAllByRoomId(roomId)
                .stream()
                .map(RoomReviewMapper::toResponse)
                .toList();
    }

    @Transactional
    public RoomReviewResponse resolveReview(Integer roomId, RoomReviewDecisionRequest request) {
        if (request.status() == RoomReviewStatus.REJECTED && (request.comment() == null || request.comment().isBlank())) {
            throw new IllegalArgument("Comment is required when rejecting a review");
        }

        RoomReviewEntity review = roomReviewRepository.findByRoomIdAndStatus(roomId, RoomReviewStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for room: " + roomId));

        review.setStatus(request.status());
        review.setComment(request.comment());
        review.setAdmin(SecurityUtils.getCurrentUser());
        review.setReviewedAt(LocalDateTime.now());

        return RoomReviewMapper.toResponse(roomReviewRepository.save(review));
    }

    public Optional<RoomReviewDetailResponse> findLatestRejectedReview(Integer roomId) {
        roomSecurityService.checkRoomAccess(roomId);
        return roomReviewRepository.findLatestByRoomId(roomId)
                .filter(r -> r.getStatus() == RoomReviewStatus.REJECTED)
                .map(RoomReviewMapper::toDetailResponse);
    }
}

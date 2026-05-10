package com.habitame.api.roomReview.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.IllegalArgument;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import com.habitame.api.room.service.RoomSecurityService;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewEntity;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import com.habitame.api.roomReview.repository.RoomReviewRepository;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomReviewServiceTest {

    @Mock
    private RoomReviewRepository roomReviewRepository;
    @Mock
    private RoomSecurityService roomSecurityService;

    @InjectMocks
    private RoomReviewService roomReviewService;

    private MockedStatic<SecurityUtils> securityUtils;
    private UserEntity admin;

    @BeforeEach
    void setUp() {
        admin = new UserEntity();
        admin.setId(99);
        admin.setRole(Role.ADMIN);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(admin);
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }


    @Test
    void addReview_ShouldCreatePendingReview() {
        RoomEntity room = buildRoom(1);

        when(roomReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomReviewService.addReview(room);

        verify(roomReviewRepository).save(argThat(review ->
                review.getRoom().equals(room) && review.getStatus() == RoomReviewStatus.PENDING
        ));
    }


    @Test
    void findById_WhenFound_ShouldReturnDetailResponse() {
        RoomEntity room = buildRoom(1);
        RoomReviewEntity review = buildReview(10, RoomReviewStatus.PENDING, room);

        when(roomReviewRepository.findById(10)).thenReturn(Optional.of(review));

        var result = roomReviewService.findById(10);

        assertThat(result).isNotNull();
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        when(roomReviewRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomReviewService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review not found: 99");
    }


    @Test
    void resolveReview_WhenApproved_ShouldSetStatusAndAdmin() {
        RoomEntity room = buildRoom(1);
        RoomReviewEntity review = buildReview(1, RoomReviewStatus.PENDING, room);
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.APPROVED, null);

        when(roomReviewRepository.findByRoomIdAndStatus(1, RoomReviewStatus.PENDING))
                .thenReturn(Optional.of(review));
        when(roomReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomReviewResponse result = roomReviewService.resolveReview(1, request);

        assertThat(review.getStatus()).isEqualTo(RoomReviewStatus.APPROVED);
        assertThat(review.getAdmin()).isEqualTo(admin);
        assertThat(review.getReviewedAt()).isNotNull();
    }

    @Test
    void resolveReview_WhenRejectedWithComment_ShouldSetCommentAndStatus() {
        RoomEntity room = buildRoom(1);
        RoomReviewEntity review = buildReview(1, RoomReviewStatus.PENDING, room);
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(
                RoomReviewStatus.REJECTED, "Fotos insuficientes"
        );

        when(roomReviewRepository.findByRoomIdAndStatus(1, RoomReviewStatus.PENDING))
                .thenReturn(Optional.of(review));
        when(roomReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomReviewService.resolveReview(1, request);

        assertThat(review.getStatus()).isEqualTo(RoomReviewStatus.REJECTED);
        assertThat(review.getComment()).isEqualTo("Fotos insuficientes");
    }

    @Test
    void resolveReview_WhenRejectedWithoutComment_ShouldThrow() {
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.REJECTED, null);

        assertThatThrownBy(() -> roomReviewService.resolveReview(1, request))
                .isInstanceOf(IllegalArgument.class)
                .hasMessageContaining("Comment is required");

        verify(roomReviewRepository, never()).save(any());
    }

    @Test
    void resolveReview_WhenRejectedWithBlankComment_ShouldThrow() {
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.REJECTED, "   ");

        assertThatThrownBy(() -> roomReviewService.resolveReview(1, request))
                .isInstanceOf(IllegalArgument.class);
    }

    @Test
    void resolveReview_WhenNoPendingReview_ShouldThrow() {
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.APPROVED, null);

        when(roomReviewRepository.findByRoomIdAndStatus(99, RoomReviewStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomReviewService.resolveReview(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void findLatestRejectedReview_WhenRejected_ShouldReturnPresent() {
        RoomEntity room = buildRoom(1);
        RoomReviewEntity review = buildReview(1, RoomReviewStatus.REJECTED, room);

        when(roomReviewRepository.findLatestByRoomId(1)).thenReturn(Optional.of(review));

        var result = roomReviewService.findLatestRejectedReview(1);

        assertThat(result).isPresent();
        verify(roomSecurityService).checkRoomAccess(1);
    }

    @Test
    void findLatestRejectedReview_WhenApproved_ShouldReturnEmpty() {
        RoomEntity room = buildRoom(1);
        RoomReviewEntity review = buildReview(1, RoomReviewStatus.APPROVED, room);

        when(roomReviewRepository.findLatestByRoomId(1)).thenReturn(Optional.of(review));

        var result = roomReviewService.findLatestRejectedReview(1);

        assertThat(result).isEmpty();
    }


    private RoomEntity buildRoom(Integer id) {
        return RoomEntity.builder()
                .id(id)
                .title("Habitación")
                .status(RoomStatus.IN_REVIEW)
                .roomAmenities(new ArrayList<>())
                .images(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();
    }

    private RoomReviewEntity buildReview(Integer id, RoomReviewStatus status, RoomEntity room) {
        RoomReviewEntity review = new RoomReviewEntity();
        review.setId(id);
        review.setStatus(status);
        review.setRoom(room);
        return review;
    }
}

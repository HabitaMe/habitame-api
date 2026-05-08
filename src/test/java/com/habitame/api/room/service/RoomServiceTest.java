package com.habitame.api.room.service;

import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import com.habitame.api.room.repository.RoomRepository;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import com.habitame.api.roomReview.service.RoomReviewService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomSecurityService roomSecurityService;
    @Mock
    private AmenityService amenityService;
    @Mock
    private PropertyService propertyService;
    @Mock
    private RoomReviewService roomReviewService;

    @InjectMocks
    private RoomService roomService;

    private MockedStatic<SecurityUtils> securityUtils;
    private UserEntity owner;
    private PropertyEntity property;

    @BeforeEach
    void setUp() {
        owner = new UserEntity();
        owner.setId(1);
        owner.setRole(Role.ARRENDADOR);

        property = PropertyEntity.builder()
                .id(10)
                .owner(owner)
                .images(new ArrayList<>())
                .propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(owner);
        securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }

    // ------------------- updateOwnerRoom -------------------

    @Test
    void updateOwnerRoom_WhenTitleChanges_ShouldTriggerReview() {
        RoomEntity room = buildRoom("Habitación doble", "Descripción", BigDecimal.valueOf(400));
        RoomOwnerRequest request = buildRequest("Habitación doble renovada", "Descripción", BigDecimal.valueOf(400));

        when(roomRepository.findByIdAndPropertyOwnerId(1, 1)).thenReturn(Optional.of(room));
        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.updateOwnerRoom(1, request);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.IN_REVIEW);
        verify(roomReviewService).addReview(room);
    }

    @Test
    void updateOwnerRoom_WhenPriceChanges_ShouldTriggerReview() {
        RoomEntity room = buildRoom("Habitación doble", "Descripción", BigDecimal.valueOf(400));
        RoomOwnerRequest request = buildRequest("Habitación doble", "Descripción", BigDecimal.valueOf(450));

        when(roomRepository.findByIdAndPropertyOwnerId(1, 1)).thenReturn(Optional.of(room));
        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.updateOwnerRoom(1, request);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.IN_REVIEW);
        verify(roomReviewService).addReview(room);
    }

    @Test
    void updateOwnerRoom_WhenOnlyFloorChanges_ShouldNotTriggerReview() {
        RoomEntity room = buildRoom("Habitación doble", "Descripción", BigDecimal.valueOf(400));
        room.setStatus(RoomStatus.ACTIVE);

        // mismo título, descripción y precio
        RoomOwnerRequest request = new RoomOwnerRequest(
                "Habitación doble", "Descripción", BigDecimal.valueOf(50), 2, BigDecimal.valueOf(400), 3, 10
        );

        when(roomRepository.findByIdAndPropertyOwnerId(1, 1)).thenReturn(Optional.of(room));
        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.updateOwnerRoom(1, request);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
        verify(roomReviewService, never()).addReview(any());
    }

    @Test
    void updateOwnerRoom_WhenRoomIsInactive_ShouldAlwaysTriggerReview() {
        RoomEntity room = buildRoom("Habitación doble", "Descripción", BigDecimal.valueOf(400));
        room.setStatus(RoomStatus.INACTIVE);

        RoomOwnerRequest request = buildRequest("Habitación doble", "Descripción", BigDecimal.valueOf(400));

        when(roomRepository.findByIdAndPropertyOwnerId(1, 1)).thenReturn(Optional.of(room));
        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.updateOwnerRoom(1, request);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.IN_REVIEW);
        verify(roomReviewService).addReview(room);
    }

    @Test
    void updateOwnerRoom_WhenNotFound_ShouldThrow() {
        RoomOwnerRequest request = buildRequest("Título", "Descripción", BigDecimal.valueOf(400));

        when(roomRepository.findByIdAndPropertyOwnerId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateOwnerRoom(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ------------------- resolveReview -------------------

    @Test
    void resolveReview_WhenApproved_ShouldSetRoomActive() {
        RoomEntity room = buildRoom("Habitación doble", "Descripción", BigDecimal.valueOf(400));
        room.setStatus(RoomStatus.IN_REVIEW);

        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.APPROVED, null);
        RoomReviewResponse reviewResponse = new RoomReviewResponse(1, "APPROVED", 1);

        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(roomReviewService.resolveReview(1, request)).thenReturn(reviewResponse);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.resolveReview(1, request);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
    }

    @Test
    void resolveReview_WhenRejected_ShouldSetRoomInactive() {
        RoomEntity room = buildRoom("Habitación doble", "Descripción", BigDecimal.valueOf(400));
        room.setStatus(RoomStatus.IN_REVIEW);

        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(
                RoomReviewStatus.REJECTED, "Fotos insuficientes"
        );
        RoomReviewResponse reviewResponse = new RoomReviewResponse(1, "REJECTED", 1);

        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(roomReviewService.resolveReview(1, request)).thenReturn(reviewResponse);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.resolveReview(1, request);

        assertThat(room.getStatus()).isEqualTo(RoomStatus.INACTIVE);
    }

    // ------------------- helpers -------------------

    private RoomEntity buildRoom(String title, String description, BigDecimal price) {
        return RoomEntity.builder()
                .id(1)
                .title(title)
                .description(description)
                .pricePerMonth(price)
                .areaM2(BigDecimal.valueOf(20))
                .maxOccupants(1)
                .floor(1)
                .status(RoomStatus.IN_REVIEW)
                .property(property)
                .roomAmenities(new ArrayList<>())
                .images(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();
    }

    private RoomOwnerRequest buildRequest(String title, String description, BigDecimal price) {
        return new RoomOwnerRequest(title, description, BigDecimal.valueOf(20), 1, price, 1, 10);
    }
}

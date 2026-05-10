package com.habitame.api.room.service;

import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.room.dto.RoomAdminDetailResponse;
import com.habitame.api.room.dto.RoomAdminRequest;
import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.dto.RoomOwnerDetailResponse;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private CityEntity city;

    @BeforeEach
    void setUp() {
        owner = new UserEntity();
        owner.setId(1);
        owner.setRole(Role.ARRENDADOR);

        city = new CityEntity();
        city.setId(5);
        city.setName("Madrid");

        property = PropertyEntity.builder()
                .id(10)
                .owner(owner)
                .address("Calle Mayor 1")
                .cityEntity(city)
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


    @Test
    void findAllPublicRooms_ShouldReturnMappedPage() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findAllByStatus(RoomStatus.ACTIVE, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(room)));

        PageResponse<RoomPublicResponse> result = roomService.findAllPublicRooms(PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findByIdPublicRoom_WhenFound_ShouldReturn() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findByIdAndStatus(1, RoomStatus.ACTIVE)).thenReturn(Optional.of(room));

        RoomPublicDetailResponse result = roomService.findByIdPublicRoom(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findByIdPublicRoom_WhenNotFound_ShouldThrow() {
        when(roomRepository.findByIdAndStatus(99, RoomStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.findByIdPublicRoom(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void findByPropertyIdPublic_ShouldReturnPage() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findAllByPropertyIdAndStatus(10, RoomStatus.ACTIVE, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(room)));

        PageResponse<RoomPublicResponse> result = roomService.findByPropertyIdPublic(10, PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findAllByOwner_ShouldReturnPage() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findAllByPropertyOwnerId(1, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(room)));

        PageResponse<RoomOwnerResponse> result = roomService.findAllByOwner(PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findMyRoomById_WhenFound_ShouldReturn() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findByIdAndPropertyOwnerId(1, 1)).thenReturn(Optional.of(room));

        RoomOwnerDetailResponse result = roomService.findMyRoomById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findMyRoomById_WhenNotFound_ShouldThrow() {
        when(roomRepository.findByIdAndPropertyOwnerId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.findMyRoomById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void addOwnerRoom_ShouldSaveAndTriggerReview() {
        RoomOwnerRequest request = buildRequest("Habitación nueva", "Desc", BigDecimal.valueOf(300));

        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomOwnerResponse result = roomService.addOwnerRoom(request);

        assertThat(result).isNotNull();
        verify(roomReviewService).addReview(any());
    }


    @Test
    void findAll_ShouldReturnAllRooms() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(room)));

        PageResponse<RoomAdminResponse> result = roomService.findAll(PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findById_WhenFound_ShouldReturnAdminDetail() {
        RoomEntity room = buildRoomWithTimestamps("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));

        RoomAdminDetailResponse result = roomService.findById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        when(roomRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void saveAdminRoom_ShouldSaveAndReturn() {
        RoomAdminRequest request = new RoomAdminRequest(
                "Habitación", "Desc", BigDecimal.valueOf(20), 2, BigDecimal.valueOf(400),
                1, 10, RoomStatus.ACTIVE, 1
        );

        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomAdminResponse result = roomService.saveAdminRoom(request);

        assertThat(result).isNotNull();
    }


    @Test
    void updateAdminRoom_WhenFound_ShouldUpdate() {
        RoomAdminRequest request = new RoomAdminRequest(
                "Habitación", "Desc", BigDecimal.valueOf(20), 2, BigDecimal.valueOf(400),
                1, 10, RoomStatus.ACTIVE, 1
        );
        RoomEntity room = buildRoomWithTimestamps("Habitación doble", "Desc", BigDecimal.valueOf(400));

        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(propertyService.findEntityById(10)).thenReturn(property);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomAdminDetailResponse result = roomService.updateAdminRoom(1, request);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void updateAdminRoom_WhenNotFound_ShouldThrow() {
        RoomAdminRequest request = new RoomAdminRequest(
                "Habitación", "Desc", BigDecimal.valueOf(20), 2, BigDecimal.valueOf(400),
                1, 10, RoomStatus.ACTIVE, 1
        );
        when(roomRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateAdminRoom(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void deleteRoom_ShouldDelete() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));

        roomService.deleteRoom(1);

        verify(roomSecurityService).checkRoomAccess(room);
        verify(roomRepository).delete(room);
    }

    @Test
    void deleteRoom_WhenNotFound_ShouldThrow() {
        when(roomRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.deleteRoom(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void addAmenities_ShouldAddToRoom() {
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        AmenityEntity amenity = AmenityEntity.builder()
                .id(1).name("WiFi").description("desc").scope(AmenityScope.ROOM).build();

        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(amenityService.findAmenityById(1)).thenReturn(amenity);
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        roomService.addAmenities(1, List.of(1));

        assertThat(room.getRoomAmenities()).contains(amenity);
    }


    @Test
    void removeAmenities_ShouldRemoveFromRoom() {
        AmenityEntity amenity = AmenityEntity.builder()
                .id(1).name("WiFi").description("desc").scope(AmenityScope.ROOM).build();
        RoomEntity room = buildRoom("Habitación doble", "Desc", BigDecimal.valueOf(400));
        room.getRoomAmenities().add(amenity);

        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(amenityService.findAmenityById(1)).thenReturn(amenity);

        roomService.removeAmenities(1, List.of(1));

        assertThat(room.getRoomAmenities()).doesNotContain(amenity);
    }


    private RoomEntity buildRoomWithTimestamps(String title, String description, BigDecimal price) {
        return RoomEntity.builder()
                .id(1)
                .title(title)
                .description(description)
                .pricePerMonth(price)
                .areaM2(BigDecimal.valueOf(20))
                .maxOccupants(1)
                .floor(1)
                .status(RoomStatus.IN_REVIEW)
                .createdAt(LocalDateTime.now())
                .property(property)
                .roomAmenities(new ArrayList<>())
                .images(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();
    }

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

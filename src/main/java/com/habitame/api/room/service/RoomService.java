package com.habitame.api.room.service;

import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.RoomMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.room.dto.RoomAdminDetailResponse;
import com.habitame.api.room.dto.RoomAdminRequest;
import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.dto.RoomFilter;
import com.habitame.api.room.dto.RoomOwnerDetailResponse;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.repository.RoomSpecification;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import com.habitame.api.room.repository.RoomRepository;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import com.habitame.api.roomReview.service.RoomReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomSecurityService roomSecurityService;
    private final AmenityService amenityService;
    private final PropertyService propertyService;
    private final RoomReviewService roomReviewService;

    public PageResponse<RoomPublicResponse> findAllPublicRooms(RoomFilter filter, Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAll(RoomSpecification.activeWith(filter, null), pageable);
        return toPageResponse(page, RoomMapper::toPublicResponse);
    }

    public RoomPublicDetailResponse findByIdPublicRoom(Integer idRoom) {
        RoomEntity roomEntity = roomRepository.findByIdAndStatus(idRoom, RoomStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Room not found: " + idRoom));
        return RoomMapper.toPublicDetailResponse(roomEntity);
    }

    public PageResponse<RoomPublicResponse> findByPropertyIdPublic(Integer idProperty, RoomFilter filter, Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAll(RoomSpecification.activeWith(filter, idProperty), pageable);
        return toPageResponse(page, RoomMapper::toPublicResponse);
    }

    public PageResponse<RoomOwnerResponse> findAllByOwner(Pageable pageable) {
        Integer ownerId = SecurityUtils.getCurrentUserId();
        Page<RoomEntity> page = roomRepository.findAllByPropertyOwnerId(ownerId, pageable);
        return toPageResponse(page, RoomMapper::toOwnerResponse);
    }

    public RoomOwnerDetailResponse findMyRoomById(Integer roomId) {
        Integer ownerId = SecurityUtils.getCurrentUserId();

        RoomEntity room = roomRepository.findByIdAndPropertyOwnerId(roomId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        return RoomMapper.toOwnerDetailResponse(room);
    }

    @Transactional
    public RoomOwnerResponse addOwnerRoom(RoomOwnerRequest request) {
        RoomEntity room = RoomMapper.ownerToEntity(
                request,
                propertyService.findEntityById(request.propertyId())
        );
        roomRepository.save(room);
        roomReviewService.addReview(room);
        return RoomMapper.toOwnerResponse(room);
    }

    @Transactional
    public RoomOwnerDetailResponse updateOwnerRoom(Integer roomId, @Valid RoomOwnerRequest request) {
        Integer ownerId = SecurityUtils.getCurrentUserId();
        RoomEntity room = roomRepository.findByIdAndPropertyOwnerId(roomId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        boolean requiresReview = !room.getTitle().equals(request.title())
                || !room.getDescription().equals(request.description())
                || !room.getPricePerMonth().equals(request.pricePerMonth())
                || room.getStatus().equals(RoomStatus.INACTIVE);

        RoomMapper.updateOwnerRoom(room, request, propertyService.findEntityById(request.propertyId()));
        room.setUpdatedBy(SecurityUtils.getCurrentUser());

        if (requiresReview) {
            room.setStatus(RoomStatus.IN_REVIEW);
            roomReviewService.addReview(room);
        }

        return RoomMapper.toOwnerDetailResponse(roomRepository.save(room));
    }

    public PageResponse<RoomAdminResponse> findAll(Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAll(pageable);
        return toPageResponse(page, RoomMapper::toAdminResponse);
    }

    public RoomAdminDetailResponse findById(Integer roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));
        return RoomMapper.toAdminDetailResponse(room);
    }

    @Transactional
    public RoomAdminResponse saveAdminRoom(RoomAdminRequest request) {
        RoomEntity room = RoomMapper.adminToEntity(
                request,
                propertyService.findEntityById(request.propertyId())
        );
        return RoomMapper.toAdminResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomAdminDetailResponse updateAdminRoom(Integer roomId, RoomAdminRequest request) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        RoomMapper.updateAdminRoom(
                room,
                request,
                propertyService.findEntityById(request.propertyId())
        );
        room.setUpdatedBy(SecurityUtils.getCurrentUser());

        return RoomMapper.toAdminDetailResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomReviewResponse resolveReview(Integer roomId, RoomReviewDecisionRequest request) {
        RoomEntity room = findEntityById(roomId);

        RoomReviewResponse response = roomReviewService.resolveReview(roomId, request);

        room.setStatus(request.status() == RoomReviewStatus.APPROVED ? RoomStatus.ACTIVE : RoomStatus.INACTIVE);

        roomRepository.save(room);

        return response;
    }

    @Transactional
    public void deleteRoom(Integer roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        roomSecurityService.checkRoomAccess(room);
        roomRepository.delete(room);
    }

    @Transactional
    public RoomOwnerResponse addAmenities(Integer roomId, List<Integer> amenityIds) {
        RoomEntity room = findEntityById(roomId);
        roomSecurityService.checkRoomAccess(room);

        List<AmenityEntity> amenities = amenityIds.stream()
                .map(amenityService::findAmenityById)
                .toList();

        room.getRoomAmenities().addAll(amenities);
        return RoomMapper.toOwnerResponse(roomRepository.save(room));
    }

    @Transactional
    public void removeAmenities(Integer roomId, List<Integer> amenityIds) {
        RoomEntity room = findEntityById(roomId);
        roomSecurityService.checkRoomAccess(room);

        List<AmenityEntity> amenities = amenityIds.stream()
                .map(amenityService::findAmenityById)
                .toList();

        room.getRoomAmenities().removeAll(amenities);
        roomRepository.save(room);
    }

    public RoomEntity findEntityById(Integer roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));
    }

    private <T> PageResponse<T> toPageResponse(Page<RoomEntity> page, Function<RoomEntity, T> mapper) {
        return new PageResponse<>(
                page.map(mapper).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

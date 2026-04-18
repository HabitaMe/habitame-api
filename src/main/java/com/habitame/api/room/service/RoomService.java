package com.habitame.api.room.service;

import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.RoomMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.room.dto.*;
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


    // -------------
    // PÚBLICO
    // -------------

    public PageResponse<RoomPublicResponse> findAllPublicRooms(Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAllByStatus(RoomStatus.ACTIVE, pageable);

        return toPageResponse(page, RoomMapper::toPublicResponse);
    }

    public RoomPublicDetailResponse findByIdPublicRoom(Integer idRoom) {
        RoomEntity roomEntity = roomRepository.findByIdAndStatus(idRoom, RoomStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Room not found: " + idRoom));
        return RoomMapper.toPublicDetailResponse(roomEntity);
    }

    public PageResponse<RoomPublicResponse> findByPropertyIdPublic(Integer idProperty, Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAllByPropertyIdAndStatus(idProperty, RoomStatus.ACTIVE, pageable);

        return toPageResponse(page, RoomMapper::toPublicResponse);
    }


    // -------------
    // OWNER
    // -------------

    /**
     * Devuelve todas las rooms del owner autenticado, paginadas.
     * Si no tiene rooms, devuelve una página vacía.
     */
    public PageResponse<RoomOwnerResponse> findAllByOwner(Pageable pageable) {
        Integer ownerId = SecurityUtils.getCurrentUserId();
        Page<RoomEntity> page = roomRepository.findAllByPropertyOwnerId(ownerId, pageable);
        return toPageResponse(page, RoomMapper::toOwnerResponse);
    }

    /**
     * Busca una room del owner autenticado por su ID.
     * Filtra por owner para evitar que un arrendador acceda a propiedades ajenas.
     *
     * @throws ResourceNotFoundException si la propiedad no existe o no pertenece al owner
     */
    public RoomOwnerDetailResponse findMyRoomById(Integer roomId) {
        Integer ownerId = SecurityUtils.getCurrentUserId();

        RoomEntity room = roomRepository.findByIdAndPropertyOwnerId(roomId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        return RoomMapper.toOwnerDetailResponse(room);
    }

    /**
     * Registra una nueva room para el owner autenticado.
     * Toda room creada entra directamente en estado {@link RoomStatus#IN_REVIEW}
     * y se genera su primer registro de revisión.
     */
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

    /**
     * Actualiza una room del owner autenticado.
     * Si se modifican el título, la descripción o el precio, la room
     * vuelve a estado {@link RoomStatus#IN_REVIEW} y se genera un nuevo registro
     * de revisión. Cambios en otros campos no afectan el estado actual.
     *
     * @throws ResourceNotFoundException si la room no existe o no pertenece al owner
     */
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


    // -------------
    // ADMIN
    // -------------


    /**
     * Devuelve todas las habitaciones del sistema sin filtros, paginadas.
     * Incluye habitaciones en cualquier estado.
     */
    public PageResponse<RoomAdminResponse> findAll(Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAll(pageable);
        return toPageResponse(page, RoomMapper::toAdminResponse);
    }

    /**
     * Busca cualquier habitacion por su ID independientemente de su estado u owner.
     *
     * @throws ResourceNotFoundException si la habitacion no existe
     */
    public RoomAdminDetailResponse findById(Integer roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));
        return RoomMapper.toAdminDetailResponse(room);
    }

    /**
     * Crea una habitacion asignándola a una propiedad existente.
     * A diferencia de {@link #addOwnerRoom}, el admin puede especificar
     * cualquier estado inicial.
     *
     * @throws ResourceNotFoundException si la propiedad no existe
     */
    @Transactional
    public RoomAdminResponse saveAdminRoom(RoomAdminRequest request) {
        RoomEntity room = RoomMapper.adminToEntity(
                request,
                propertyService.findEntityById(request.propertyId())
        );
        return RoomMapper.toAdminResponse(roomRepository.save(room));
    }

    /**
     * Actualiza cualquier habitacion del sistema, incluyendo su propiedad.
     * No genera revisión automáticamente — el admin gestiona el estado de forma manual.
     *
     * @throws ResourceNotFoundException si la propiedad o la habitacion no existen
     */
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

    /**
     * Resuelve la review pendiente de una habitacion y actualiza su status en consecuencia.
     * Coordina {@link RoomReviewService} y {@link RoomRepository} en la misma transacción.
     */
    @Transactional
    public RoomReviewResponse resolveReview(Integer roomId, RoomReviewDecisionRequest request) {
        RoomEntity room = findEntityById(roomId);

        RoomReviewResponse response = roomReviewService.resolveReview(roomId, request);

        room.setStatus(request.status() == RoomReviewStatus.APPROVED ? RoomStatus.ACTIVE : RoomStatus.INACTIVE);

        roomRepository.save(room);

        return response;
    }

    // -------------
    // COMPARTIDO
    // -------------


    /**
     * Elimina una habitacion. Accesible para el owner de la propiedad y para admins.
     * La verificación de permisos se delega a {@link RoomSecurityService#checkRoomAccess}.
     *
     * @throws ResourceNotFoundException si la habitacion no existe
     * @throws ForbiddenException        si el usuario no tiene permisos sobre la habitacion
     */
    @Transactional
    public void deleteRoom(Integer roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        roomSecurityService.checkRoomAccess(room);
        roomRepository.delete(room);
    }

    /**
     * Agrega amenidades a una habitacion.
     * No filtra duplicados
     *
     * @throws ResourceNotFoundException si la habitacion o alguna amenidad no existe
     * @throws ForbiddenException        si el usuario no tiene permisos sobre la habitacion
     */
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

    /**
     * Elimina amenidades de una propiedad.
     * Si alguna amenidad del listado no estaba asignada, se ignora sin lanzar error.
     *
     * @throws ResourceNotFoundException si la habitacion o alguna amenidad no existe
     * @throws ForbiddenException        si el usuario no tiene permisos sobre la habitacion
     */
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


    // -------------
    // INTERNO
    // -------------

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

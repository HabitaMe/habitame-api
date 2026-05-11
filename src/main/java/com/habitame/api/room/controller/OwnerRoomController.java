package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomOwnerDetailResponse;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.service.RoomReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/owner/rooms")
@PreAuthorize("hasRole('ARRENDADOR')")
@Tag(name = "Mis habitaciones (Arrendador)", description = "Gestión de las habitaciones del arrendador autenticado. Solo accesible para ARRENDADOR.")
@SecurityRequirement(name = "bearerAuth")
public class OwnerRoomController extends AbstractRoomController {

    public OwnerRoomController(RoomImageService roomImageService,
                               RoomService roomService, RoomReviewService roomReviewService) {
        super(roomImageService, roomService, roomReviewService);
    }

    @GetMapping
    @Operation(summary = "Mis habitaciones", description = "Devuelve todas las habitaciones del arrendador autenticado, en cualquier estado.")
    public ResponseEntity<PageResponse<RoomOwnerResponse>> findMyRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllByOwner(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de una habitación mía", description = "Devuelve el detalle completo de una habitación del arrendador autenticado, incluyendo el historial de revisiones.")
    public ResponseEntity<RoomOwnerDetailResponse> findMyRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.findMyRoomById(id));
    }

    @PostMapping
    @Operation(
            summary = "Publicar habitación",
            description = "Crea una nueva habitación dentro de una de las propiedades del arrendador. Queda en estado IN_REVIEW hasta que un admin la apruebe o rechace."
    )
    public ResponseEntity<Void> addOwnerRoom(@RequestBody @Valid RoomOwnerRequest request) {
        RoomOwnerResponse response = roomService.addOwnerRoom(request);
        URI location = URI.create("v1/owner/rooms/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar habitación",
            description = "Actualiza los datos de una habitación. Si se modifican el título, la descripción o el precio, vuelve a estado IN_REVIEW automáticamente."
    )
    public ResponseEntity<RoomOwnerDetailResponse> updateOwnerRoom(
            @PathVariable Integer id,
            @RequestBody @Valid RoomOwnerRequest request) {
        return ResponseEntity.ok(roomService.updateOwnerRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar habitación", description = "Elimina permanentemente una habitación del arrendador autenticado.")
    public void deleteOwnerRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/{id}/reviews/latest")
    @Operation(summary = "Ver última revisión rechazada", description = "Devuelve la revisión más reciente si fue rechazada, para que el arrendador sepa qué corregir. Devuelve 204 si la última revisión no fue un rechazo.")
    public ResponseEntity<RoomReviewDetailResponse> findLatestRejectedReview(@PathVariable Integer id) {
        return roomReviewService.findLatestRejectedReview(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}

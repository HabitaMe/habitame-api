package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomAdminDetailResponse;
import com.habitame.api.room.dto.RoomAdminRequest;
import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Habitaciones (Admin)", description = "Gestión completa de habitaciones. Incluye aprobar o rechazar las revisiones pendientes. Solo accesible para ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class AdminRoomController extends AbstractRoomController {

    public AdminRoomController(RoomImageService roomImageService,
                               RoomService roomService, RoomReviewService roomReviewService) {
        super(roomImageService, roomService, roomReviewService);
    }

    @GetMapping
    @Operation(summary = "Listar todas las habitaciones", description = "Devuelve todas las habitaciones del sistema sin filtros de estado ni de owner.")
    public ResponseEntity<PageResponse<RoomAdminResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(roomService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de una habitación", description = "Devuelve el detalle completo de cualquier habitación, independientemente de su estado u owner.")
    public ResponseEntity<RoomAdminDetailResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear habitación", description = "Crea una habitación asignándola a una propiedad existente. El admin puede establecer el estado directamente.")
    public ResponseEntity<Void> saveRoom(@RequestBody @Valid RoomAdminRequest request) {
        RoomAdminResponse response = roomService.saveAdminRoom(request);
        URI location = URI.create("v1/admin/rooms/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar habitación", description = "Modifica cualquier campo de una habitación, incluida la propiedad a la que pertenece. No genera revisión automática.")
    public ResponseEntity<RoomAdminDetailResponse> updateAdminRoom(
            @PathVariable Integer id,
            @RequestBody @Valid RoomAdminRequest request) {
        return ResponseEntity.ok(roomService.updateAdminRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar habitación")
    public void deleteAdminRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Ver historial de revisiones de una habitación", description = "Devuelve todas las revisiones de la habitación, ordenadas de más reciente a más antigua.")
    public ResponseEntity<List<RoomReviewResponse>> findReviews(@PathVariable Integer id) {
        return ResponseEntity.ok(roomReviewService.findAllByRoomId(id));
    }

    @PatchMapping("/{id}/reviews/resolve")
    @Operation(
            summary = "Aprobar o rechazar una habitación",
            description = "Resuelve la revisión pendiente de la habitación. Si se aprueba, pasa a ACTIVE. Si se rechaza, pasa a INACTIVE y el comentario es obligatorio para que el arrendador sepa qué corregir."
    )
    public ResponseEntity<RoomReviewResponse> resolveReview(@PathVariable Integer id, @RequestBody @Valid RoomReviewDecisionRequest request) {
        return ResponseEntity.ok(roomService.resolveReview(id, request));
    }
}

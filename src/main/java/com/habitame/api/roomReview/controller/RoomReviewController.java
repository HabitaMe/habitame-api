package com.habitame.api.roomReview.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import com.habitame.api.roomReview.service.RoomReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/room-reviews")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Revisiones de habitaciones (Admin)", description = "Historial de todas las revisiones de habitaciones. Solo accesible para ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class RoomReviewController {

    private final RoomReviewService roomReviewService;

    @GetMapping
    @Operation(summary = "Listar todas las revisiones", description = "Devuelve el historial completo de revisiones de habitaciones, ordenado de más reciente a más antiguo.")
    public ResponseEntity<PageResponse<RoomReviewResponse>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(roomReviewService.getReviews(pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar revisiones por estado", description = "Devuelve únicamente las revisiones con el estado indicado: PENDING, APPROVED o REJECTED.")
    public ResponseEntity<PageResponse<RoomReviewResponse>> getReviews(@PathVariable RoomReviewStatus status, Pageable pageable) {
        return ResponseEntity.ok(roomReviewService.getReviewsByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver revisión por ID", description = "Devuelve el detalle de una revisión concreta, incluyendo el comentario del admin si fue rechazada.")
    public ResponseEntity<RoomReviewDetailResponse> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomReviewService.findById(id));
    }
}

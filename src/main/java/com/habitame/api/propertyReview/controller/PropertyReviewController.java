package com.habitame.api.propertyReview.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.propertyReview.service.PropertyReviewService;
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
@RequestMapping("/v1/admin/property-reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Revisiones de propiedades (Admin)", description = "Historial de todas las revisiones de propiedades. Solo accesible para ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class PropertyReviewController {

    private final PropertyReviewService propertyReviewService;

    @GetMapping
    @Operation(summary = "Listar todas las revisiones", description = "Devuelve el historial completo de revisiones de propiedades, ordenado de más reciente a más antiguo.")
    public ResponseEntity<PageResponse<PropertyReviewResponse>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(propertyReviewService.getReviews(pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar revisiones por estado", description = "Devuelve únicamente las revisiones con el estado indicado: PENDING, APPROVED o REJECTED.")
    public ResponseEntity<PageResponse<PropertyReviewResponse>> getReviews(@PathVariable PropertyReviewStatus status, Pageable pageable) {
        return ResponseEntity.ok(propertyReviewService.getReviewsByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver revisión por ID", description = "Devuelve el detalle de una revisión concreta, incluyendo el comentario del admin si fue rechazada.")
    public ResponseEntity<PropertyReviewDetailResponse> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyReviewService.findById(id));
    }
}

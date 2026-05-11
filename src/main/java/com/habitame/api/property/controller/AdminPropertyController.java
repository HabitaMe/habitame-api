package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.service.PropertyReviewService;
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
@RequestMapping("/v1/admin/properties")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Propiedades (Admin)", description = "Gestión completa de propiedades. Incluye aprobar o rechazar las revisiones pendientes. Solo accesible para ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class AdminPropertyController extends AbstractPropertyController {

    private final PropertyReviewService propertyReviewService;

    public AdminPropertyController(PropertyImageService propertyImageService,
                                   PropertyService propertyService, PropertyReviewService propertyReviewService) {
        super(propertyImageService, propertyService);
        this.propertyReviewService = propertyReviewService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las propiedades", description = "Devuelve todas las propiedades del sistema sin filtros de estado ni de owner.")
    public ResponseEntity<PageResponse<PropertyAdminResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de una propiedad", description = "Devuelve el detalle completo de cualquier propiedad, independientemente de su estado u owner.")
    public ResponseEntity<PropertyAdminDetailResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear propiedad", description = "Crea una propiedad asignándola a un owner existente. El admin puede establecer el estado directamente.")
    public ResponseEntity<Void> saveProperty(@RequestBody @Valid PropertyAdminRequest request) {
        PropertyAdminResponse response = propertyService.saveAdminProperty(request);
        URI location = URI.create("v1/admin/properties/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar propiedad", description = "Modifica cualquier campo de una propiedad, incluido el owner o la ciudad. No genera revisión automática.")
    public ResponseEntity<PropertyAdminDetailResponse> updateAdminProperty(
            @PathVariable Integer id,
            @RequestBody @Valid PropertyAdminRequest request) {
        return ResponseEntity.ok(propertyService.updateAdminProperty(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar propiedad")
    public void deleteAdminProperty(@PathVariable Integer id) {
        propertyService.deleteProperty(id);
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Ver historial de revisiones de una propiedad", description = "Devuelve todas las revisiones de la propiedad, ordenadas de más reciente a más antigua.")
    public ResponseEntity<List<PropertyReviewResponse>> findReviews(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyReviewService.findAllByPropertyId(id));
    }

    @PatchMapping("/{id}/reviews/resolve")
    @Operation(
            summary = "Aprobar o rechazar una propiedad",
            description = "Resuelve la revisión pendiente de la propiedad. Si se aprueba, pasa a ACTIVE. Si se rechaza, pasa a INACTIVE y el comentario es obligatorio para que el arrendador sepa qué corregir."
    )
    public ResponseEntity<PropertyReviewResponse> resolveReview(@PathVariable Integer id, @RequestBody @Valid PropertyReviewDecisionRequest request) {
        return ResponseEntity.ok(propertyService.resolveReview(id, request));
    }
}

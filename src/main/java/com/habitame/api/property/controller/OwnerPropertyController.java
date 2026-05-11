package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/owner/properties")
@PreAuthorize("hasRole('ARRENDADOR')")
@Tag(name = "Mis propiedades (Arrendador)", description = "Gestión de las propiedades del arrendador autenticado. Solo accesible para ARRENDADOR.")
@SecurityRequirement(name = "bearerAuth")
public class OwnerPropertyController extends AbstractPropertyController {

    private final PropertyReviewService propertyReviewService;

    public OwnerPropertyController(PropertyImageService propertyImageService,
                                   PropertyService propertyService, PropertyReviewService propertyReviewService) {
        super(propertyImageService, propertyService);
        this.propertyReviewService = propertyReviewService;
    }

    @GetMapping
    @Operation(summary = "Mis propiedades", description = "Devuelve todas las propiedades del arrendador autenticado, en cualquier estado.")
    public ResponseEntity<PageResponse<PropertyOwnerResponse>> findMyProperties(Pageable pageable) {
        return ResponseEntity.ok(propertyService.findAllByOwner(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de una propiedad mía", description = "Devuelve el detalle completo de una propiedad del arrendador autenticado, incluyendo el historial de revisiones.")
    public ResponseEntity<PropertyOwnerDetailResponse> findMyPropertyById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyService.findMyPropertyById(id));
    }

    @PostMapping
    @Operation(
            summary = "Publicar propiedad",
            description = "Crea una nueva propiedad. Queda en estado IN_REVIEW hasta que un admin la apruebe o rechace."
    )
    public ResponseEntity<Void> addOwnerProperty(@RequestBody @Valid PropertyOwnerRequest request) {
        PropertyOwnerResponse response = propertyService.addOwnerProperty(request);
        URI location = URI.create("v1/owner/properties/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar propiedad",
            description = "Actualiza los datos de una propiedad. Si se modifican el título, la descripción o la dirección, vuelve a estado IN_REVIEW automáticamente."
    )
    public ResponseEntity<PropertyOwnerDetailResponse> updateOwnerProperty(
            @PathVariable Integer id,
            @RequestBody @Valid PropertyOwnerRequest request) {
        return ResponseEntity.ok(propertyService.updateOwnerProperty(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar propiedad", description = "Elimina permanentemente una propiedad del arrendador autenticado.")
    public void deleteOwnerProperty(@PathVariable Integer id) {
        propertyService.deleteProperty(id);
    }

    @GetMapping("/{id}/reviews/latest")
    @Operation(summary = "Ver última revisión rechazada", description = "Devuelve la revisión más reciente si fue rechazada, para que el arrendador sepa qué corregir. Devuelve 204 si la última revisión no fue un rechazo.")
    public ResponseEntity<PropertyReviewDetailResponse> findLatestRejectedReview(@PathVariable Integer id) {
        return propertyReviewService.findLatestRejectedReview(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}

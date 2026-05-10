package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyFilter;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/public/properties")
@RequiredArgsConstructor
@Tag(name = "Propiedades (público)", description = "Búsqueda de propiedades disponibles. No requiere autenticación.")
public class PublicPropertyController {

    private final PropertyService propertyService;

    @GetMapping
    @Operation(
            summary = "Buscar propiedades",
            description = "Devuelve las propiedades con estado ACTIVE. Se puede filtrar por ciudad (cityId) y tipo de inmueble (type). Soporta paginación y ordenación."
    )
    public ResponseEntity<PageResponse<PropertyPublicResponse>> getPropertyList(
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) String type,
            Pageable pageable) {
        return ResponseEntity.ok(propertyService.findPublicProperties(new PropertyFilter(cityId, type), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver detalle de una propiedad", description = "Devuelve la información completa de una propiedad activa: imágenes, amenidades y habitaciones disponibles.")
    public ResponseEntity<PropertyPublicDetailResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyService.findPublicPropertyById(id));
    }
}

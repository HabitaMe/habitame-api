package com.habitame.api.amenities.controller;

import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@RestController
@RequestMapping("/v1/amenities")
@RequiredArgsConstructor
@Tag(name = "Amenidades", description = "Catálogo de comodidades disponibles para propiedades y habitaciones (WiFi, parking, piscina, etc.). Los GETs son públicos; crear, editar y eliminar requiere ADMIN.")
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    @Operation(summary = "Listar todas las amenidades", description = "Devuelve todas las amenidades disponibles sin distinguir si son de propiedad o de habitación.")
    public ResponseEntity<List<AmenityResponse>> findAmenities() {
        return ResponseEntity.ok(amenityService.findAmenities());
    }

    @GetMapping("/scope/{scope}")
    @Operation(summary = "Filtrar amenidades por ámbito", description = "Devuelve solo las amenidades del ámbito indicado: PROPERTY (para propiedades) o ROOM (para habitaciones).")
    public ResponseEntity<List<AmenityResponse>> getAmenitiesByScope(@PathVariable AmenityScope scope) {
        return ResponseEntity.ok(amenityService.findAmenitiesByScope(scope));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear amenidad", description = "Crea una nueva amenidad en el catálogo. Solo ADMIN.")
    public ResponseEntity<Void> saveAmenity(@RequestBody @Valid AmenityRequest request) {
        AmenityResponse amenityResponse = amenityService.saveAmenity(request);
        URI location = URI.create("v1/amenities/" + amenityResponse.id());
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar amenidad", description = "Modifica el nombre, descripción o ámbito de una amenidad existente. Solo ADMIN.")
    public ResponseEntity<AmenityResponse> updateAmenity(@PathVariable Integer id, @RequestBody @Valid AmenityRequest request) {
        return ResponseEntity.ok(amenityService.updateAmenity(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar amenidad", description = "Elimina una amenidad del catálogo. Solo ADMIN.")
    public void deleteAmenity(@PathVariable Integer id) {
        amenityService.deleteAmenity(id);
    }
}

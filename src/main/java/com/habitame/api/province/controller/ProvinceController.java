package com.habitame.api.province.controller;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.service.ProvinceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/v1/provinces")
@RequiredArgsConstructor
@Tag(name = "Provincias", description = "Catálogo de provincias. Los GETs son públicos. Crear, editar y eliminar requiere ADMIN.")
public class ProvinceController {

    private final ProvinceService provinceService;
    private final CityService cityService;

    @GetMapping
    @Operation(summary = "Listar provincias", description = "Devuelve todas las provincias disponibles en la plataforma.")
    public ResponseEntity<PageResponse<ProvinceResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(provinceService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver provincia por ID")
    public ResponseEntity<ProvinceResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(provinceService.findById(id));
    }

    @GetMapping("/{id}/cities")
    @Operation(summary = "Listar ciudades de una provincia", description = "Devuelve las ciudades de la provincia indicada. Útil para los selectores en cascada del formulario.")
    public ResponseEntity<PageResponse<CityResponse>> findCitiesByProvince(@PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(cityService.findByProvince(id, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear provincia", description = "Añade una nueva provincia asignándola a un país. Solo ADMIN.")
    public ResponseEntity<Void> addProvince(@Valid @RequestBody ProvinceRequest request) {
        ProvinceResponse provinceResponse = provinceService.addProvince(request);
        URI location = URI.create("/v1/provinces/" + provinceResponse.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar provincia", description = "Modifica el nombre o el país de una provincia. Solo ADMIN.")
    public ResponseEntity<ProvinceResponse> updateProvince(@PathVariable Integer id, @Valid @RequestBody ProvinceRequest request) {
        return ResponseEntity.ok(provinceService.updateProvince(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar provincia", description = "Elimina una provincia del catálogo. Solo ADMIN.")
    public void deleteProvince(@PathVariable Integer id) {
        provinceService.deleteProvince(id);
    }
}

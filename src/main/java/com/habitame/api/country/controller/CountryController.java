package com.habitame.api.country.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.service.CountryService;
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
@RequestMapping("/v1/countries")
@RequiredArgsConstructor
@Tag(name = "Países", description = "Catálogo de países. Los GETs son públicos. Crear, editar y eliminar requiere ADMIN.")
public class CountryController {

    private final CountryService countryService;
    private final ProvinceService provinceService;

    @GetMapping
    @Operation(summary = "Listar países", description = "Devuelve todos los países disponibles en la plataforma.")
    public ResponseEntity<PageResponse<CountryResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(countryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver país por ID")
    public CountryResponse findById(@PathVariable Integer id) {
        return countryService.findById(id);
    }

    @GetMapping("/{id}/provinces")
    @Operation(summary = "Listar provincias de un país", description = "Devuelve las provincias del país indicado. Útil para los selectores en cascada del formulario.")
    public ResponseEntity<PageResponse<ProvinceResponse>> findByCountryId(@PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(provinceService.findByCountry(id, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear país", description = "Añade un nuevo país al catálogo. Solo ADMIN.")
    public ResponseEntity<Void> addCountry(@Valid @RequestBody CountryRequest request) {
        CountryResponse countryResponse = countryService.addCountry(request);
        URI location = URI.create("/v1/countries/" + countryResponse.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar país", description = "Modifica el nombre de un país. Solo ADMIN.")
    public ResponseEntity<CountryResponse> updateCountry(@PathVariable Integer id, @Valid @RequestBody CountryRequest request) {
        return ResponseEntity.ok(countryService.updateCountry(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar país", description = "Elimina un país del catálogo. Solo ADMIN.")
    public void deleteCountry(@PathVariable Integer id) {
        countryService.deleteCountry(id);
    }
}

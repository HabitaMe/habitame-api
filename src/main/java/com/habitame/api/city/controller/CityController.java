package com.habitame.api.city.controller;

import com.habitame.api.city.dto.CityRequest;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.wrapper.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<PageResponse<CityResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(cityService.findAll(pageable));
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<CityResponse> findById(@PathVariable Integer cityId) {
        return ResponseEntity.ok(cityService.findById(cityId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> saveCity(@Valid @RequestBody CityRequest request) {
        CityResponse cityResponse = cityService.saveCity(request);
        URI location = URI.create("api/cities/" + cityResponse.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{cityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityResponse> updateCity(@PathVariable Integer cityId, @Valid @RequestBody CityRequest request) {
        return ResponseEntity.ok(cityService.updateCity(cityId, request));
    }

    @DeleteMapping("/{cityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Integer cityId) {
        cityService.deleteCity(cityId);
    }
}

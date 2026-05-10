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
@RequestMapping("/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<PageResponse<CityResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(cityService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cityService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> saveCity(@Valid @RequestBody CityRequest request) {
        CityResponse cityResponse = cityService.saveCity(request);
        URI location = URI.create("v1/cities/" + cityResponse.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityResponse> updateCity(@PathVariable Integer id, @Valid @RequestBody CityRequest request) {
        return ResponseEntity.ok(cityService.updateCity(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
    }
}

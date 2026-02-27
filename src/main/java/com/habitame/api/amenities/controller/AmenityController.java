package com.habitame.api.amenities.controller;

import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    public ResponseEntity<List<AmenityResponse>> findAmenities() {
        return ResponseEntity.ok(amenityService.findAmenities());
    }

    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<AmenityResponse>> getAmenitiesByScope(@PathVariable AmenityScope scope){
        return ResponseEntity.ok(amenityService.findAmenitiesByScope(scope));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> saveAmenity(@RequestBody @Valid AmenityRequest request) {
        AmenityResponse amenityResponse = amenityService.saveAmenity(request);
        URI location = URI.create("api/amenities/" + amenityResponse.getId());
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{amenityId}")
    public ResponseEntity<AmenityResponse> updateAmenity(@PathVariable Integer amenityId, @RequestBody @Valid AmenityRequest request) {
        return ResponseEntity.ok(amenityService.updateAmenity(amenityId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{amenityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAmenity(@PathVariable Integer amenityId) {
        amenityService.deleteAmenity(amenityId);
    }
}

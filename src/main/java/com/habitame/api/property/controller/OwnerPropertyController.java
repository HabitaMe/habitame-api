package com.habitame.api.property.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/owner/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ARRENDADOR')")
public class OwnerPropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<PageResponse<PropertyOwnerResponse>> findMyProperties(Pageable pageable){
        return ResponseEntity.ok(propertyService.findAllByOwner(pageable));
    }

    @GetMapping("/{idProperty}")
    public ResponseEntity<PropertyOwnerDetailResponse> findMyPropertyById(@PathVariable Integer idProperty){
        return ResponseEntity.ok(propertyService.findMyPropertyById(idProperty));
    }

    @PostMapping
    public ResponseEntity<Void> addOwnerProperty(@RequestBody @Valid PropertyOwnerRequest propertyOwnerRequest){
        PropertyOwnerResponse propertyOwnerResponse = propertyService.addOwnerProperty(propertyOwnerRequest);
        URI location = URI.create("api/owner/properties/" + propertyOwnerResponse.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idProperty}")
    public ResponseEntity<PropertyOwnerDetailResponse> updateOwnerProperty(@PathVariable Integer idProperty, @RequestBody @Valid PropertyOwnerRequest propertyOwnerRequest){
        return ResponseEntity.ok(propertyService.updateOwnerProperty(idProperty, propertyOwnerRequest));
    }

    @DeleteMapping("/{idProperty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnerProperty(@PathVariable Integer idProperty){
        propertyService.deleteOwnerProperty(idProperty);
    }
}

package com.habitame.api.province.controller;

import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.service.ProvinceService;
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
@RequestMapping("/api/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;
    private final CityService cityService;

    @GetMapping
    public ResponseEntity<PageResponse<ProvinceResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(provinceService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinceResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(provinceService.findById(id));
    }

    @GetMapping("/{id}/cities")
    public ResponseEntity<PageResponse<CityResponse>> findCitiesByProvince(@PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(cityService.findByProvince(id, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addProvince(@Valid @RequestBody ProvinceRequest request) {
        ProvinceResponse provinceResponse = provinceService.addProvince(request);
        URI location = URI.create("/api/provinces/" + provinceResponse.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProvinceResponse> updateProvince(@PathVariable Integer id, @Valid @RequestBody ProvinceRequest request) {
        return ResponseEntity.ok(provinceService.updateProvince(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProvince(@PathVariable Integer id) {
        provinceService.deleteProvince(id);
    }
}

package com.habitame.api.country.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.service.CountryService;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.service.ProvinceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    private final ProvinceService provinceService;

    @GetMapping
    public ResponseEntity<PageResponse<CountryResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(countryService.findAll(pageable));
    }

    @GetMapping("/{countryId}")
    public CountryResponse findById(@PathVariable Integer countryId) {
        return countryService.findById(countryId);
    }

    @GetMapping("/{countryId}/provinces")
    public ResponseEntity<PageResponse<ProvinceResponse>> findByCountryId(@PathVariable Integer countryId, Pageable pageable) {
        return ResponseEntity.ok(provinceService.findByCountry(countryId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addCountry(@Valid @RequestBody CountryRequest request) {
        CountryResponse countryResponse = countryService.addCountry(request);
        URI location = URI.create("/api/countries/" + countryResponse.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{countryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CountryResponse> updateCountry(@PathVariable Integer countryId, @Valid @RequestBody CountryRequest request) {
        return ResponseEntity.ok(countryService.updateCountry(countryId, request));
    }

    @DeleteMapping("/{countryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCountry(@PathVariable Integer countryId) {
        countryService.deleteCountry(countryId);
    }
}

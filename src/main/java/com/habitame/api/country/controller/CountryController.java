package com.habitame.api.country.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.service.CountryService;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.entity.ProvinceEntity;
import com.habitame.api.province.service.ProvinceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    private final ProvinceService provinceService;

    @GetMapping
    public ResponseEntity<PageResponse<CountryResponse>> findAll(Pageable pageable){
        return ResponseEntity.ok(countryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public CountryResponse findById(@PathVariable Integer id){
        return countryService.findById(id);
    }

    @GetMapping("/{countryId}/provinces")
    public ResponseEntity<PageResponse<ProvinceResponse>> findByCountryId(@PathVariable Integer countryId, Pageable pageable){
        return ResponseEntity.ok(provinceService.findByCountry(countryId, pageable));
    }

    @PostMapping
    public ResponseEntity<Void> addCountry(@Valid @RequestBody CountryRequest countryRequest){
        CountryResponse countryResponse = countryService.addCountry(countryRequest);
        URI location = URI.create("/api/countries/" + countryResponse.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{countryId}")
    public ResponseEntity<CountryResponse> updateCountry(@PathVariable Integer countryId, @Valid @RequestBody CountryRequest countryRequest){
        return ResponseEntity.ok(countryService.updateCountry(countryId, countryRequest));
    }

    @DeleteMapping("/{countryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCountry(@PathVariable Integer countryId){
        countryService.deleteCountry(countryId);
    }
}

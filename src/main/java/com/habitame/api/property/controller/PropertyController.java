package com.habitame.api.property.controller;

import com.habitame.api.property.dto.PropertiesResponse;
import com.habitame.api.property.dto.PropertyRequest;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertiesResponse>> getPropertyList(){
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @PostMapping
    public void createProperty(@RequestBody PropertyRequest propertyRequest){
        propertyService.createProperty(propertyRequest);
    }
}
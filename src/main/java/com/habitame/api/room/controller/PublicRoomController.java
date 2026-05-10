package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomFilter;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/public/rooms")
@RequiredArgsConstructor
public class PublicRoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<PageResponse<RoomPublicResponse>> findAllPublicRooms(
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minOccupants,
            Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllPublicRooms(
                new RoomFilter(cityId, minPrice, maxPrice, minOccupants), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomPublicDetailResponse> findByIdPublicRoom(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.findByIdPublicRoom(id));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<PageResponse<RoomPublicResponse>> findByPropertyIdPublic(
            @PathVariable Integer propertyId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minOccupants,
            Pageable pageable) {
        return ResponseEntity.ok(roomService.findByPropertyIdPublic(
                propertyId, new RoomFilter(null, minPrice, maxPrice, minOccupants), pageable));
    }
}

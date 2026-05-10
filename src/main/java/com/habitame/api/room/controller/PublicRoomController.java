package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomFilter;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/v1/public/rooms")
@RequiredArgsConstructor
@Tag(name = "Habitaciones (público)", description = "Búsqueda de habitaciones disponibles. No requiere autenticación.")
public class PublicRoomController {
    private final RoomService roomService;

    @GetMapping
    @Operation(
            summary = "Buscar habitaciones",
            description = "Devuelve las habitaciones con estado ACTIVE. Filtros opcionales: ciudad (cityId), precio mínimo (minPrice), precio máximo (maxPrice) y ocupantes mínimos (minOccupants). Soporta paginación y ordenación."
    )
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
    @Operation(summary = "Ver detalle de una habitación", description = "Devuelve la información completa de una habitación activa: imágenes, amenidades y datos de la propiedad a la que pertenece.")
    public ResponseEntity<RoomPublicDetailResponse> findByIdPublicRoom(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.findByIdPublicRoom(id));
    }

    @GetMapping("/property/{propertyId}")
    @Operation(
            summary = "Buscar habitaciones de una propiedad",
            description = "Devuelve las habitaciones activas de una propiedad concreta. Admite los mismos filtros de precio y ocupantes que el listado general."
    )
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

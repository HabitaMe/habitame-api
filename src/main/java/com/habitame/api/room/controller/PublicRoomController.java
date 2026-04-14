package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class PublicRoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<PageResponse<RoomPublicResponse>> findAllPublicRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllPublicRooms(pageable));
    }

    @GetMapping("/{idRoom}")
    public ResponseEntity<RoomPublicDetailResponse> findByIdPublicRoom(@PathVariable Integer idRoom) {
        return ResponseEntity.ok(roomService.findByIdPublicRoom(idRoom));
    }

    @GetMapping("/property/{idProperty}")
    public ResponseEntity<PageResponse<RoomPublicResponse>> findByPropertyIdPublic(@PathVariable Integer idProperty, Pageable pageable) {
        return ResponseEntity.ok(roomService.findByPropertyIdPublic(idProperty,  pageable));
    }
}

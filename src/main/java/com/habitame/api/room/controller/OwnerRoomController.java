package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomOwnerDetailResponse;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.dto.RoomReviewDetailResponse;
import com.habitame.api.roomReview.service.RoomReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/owner/rooms")
public class OwnerRoomController extends AbstractRoomController {

    public OwnerRoomController(RoomImageService roomImageService,
                               RoomService roomService, RoomReviewService roomReviewService) {
        super(roomImageService, roomService, roomReviewService);
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoomOwnerResponse>> findMyRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllByOwner(pageable));
    }

    @GetMapping("/{idRoom}")
    public ResponseEntity<RoomOwnerDetailResponse> findMyRoomById(@PathVariable Integer idRoom) {
        return ResponseEntity.ok(roomService.findMyRoomById(idRoom));
    }

    @PostMapping
    public ResponseEntity<Void> addOwnerRoom(@RequestBody @Valid RoomOwnerRequest request) {
        RoomOwnerResponse response = roomService.addOwnerRoom(request);
        URI location = URI.create("api/owner/rooms/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idRoom}")
    public ResponseEntity<RoomOwnerDetailResponse> updateOwnerRoom(
            @PathVariable Integer idRoom,
            @RequestBody @Valid RoomOwnerRequest request) {
        return ResponseEntity.ok(roomService.updateOwnerRoom(idRoom, request));
    }

    @DeleteMapping("/{idRoom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnerRoom(@PathVariable Integer idRoom) {
        roomService.deleteRoom(idRoom);
    }

    @GetMapping("/{idRoom}/reviews/latest")
    public ResponseEntity<RoomReviewDetailResponse> findLatestRejectedReview(@PathVariable Integer idRoom) {
        return roomReviewService.findLatestRejectedReview(idRoom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}

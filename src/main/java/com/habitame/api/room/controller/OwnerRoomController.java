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
@RequestMapping("/v1/owner/rooms")
@PreAuthorize("hasRole('ARRENDADOR')")
public class OwnerRoomController extends AbstractRoomController {

    public OwnerRoomController(RoomImageService roomImageService,
                               RoomService roomService, RoomReviewService roomReviewService) {
        super(roomImageService, roomService, roomReviewService);
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoomOwnerResponse>> findMyRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllByOwner(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomOwnerDetailResponse> findMyRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.findMyRoomById(id));
    }

    @PostMapping
    public ResponseEntity<Void> addOwnerRoom(@RequestBody @Valid RoomOwnerRequest request) {
        RoomOwnerResponse response = roomService.addOwnerRoom(request);
        URI location = URI.create("v1/owner/rooms/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomOwnerDetailResponse> updateOwnerRoom(
            @PathVariable Integer id,
            @RequestBody @Valid RoomOwnerRequest request) {
        return ResponseEntity.ok(roomService.updateOwnerRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnerRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/{id}/reviews/latest")
    public ResponseEntity<RoomReviewDetailResponse> findLatestRejectedReview(@PathVariable Integer id) {
        return roomReviewService.findLatestRejectedReview(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}

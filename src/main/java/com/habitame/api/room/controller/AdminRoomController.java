package com.habitame.api.room.controller;

import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomAdminDetailResponse;
import com.habitame.api.room.dto.RoomAdminRequest;
import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.service.RoomReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController extends AbstractRoomController {

    public AdminRoomController(RoomImageService roomImageService,
                               RoomService roomService, RoomReviewService roomReviewService) {
        super(roomImageService, roomService, roomReviewService);
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoomAdminResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(roomService.findAll(pageable));
    }

    @GetMapping("/{idRoom}")
    public ResponseEntity<RoomAdminDetailResponse> findById(@PathVariable Integer idRoom) {
        return ResponseEntity.ok(roomService.findById(idRoom));
    }

    @PostMapping
    public ResponseEntity<Void> saveRoom(@RequestBody @Valid RoomAdminRequest request) {
        RoomAdminResponse response = roomService.saveAdminRoom(request);
        URI location = URI.create("api/admin/rooms/" + response.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idRoom}")
    public ResponseEntity<RoomAdminDetailResponse> updateAdminRoom(
            @PathVariable Integer idRoom,
            @RequestBody @Valid RoomAdminRequest request) {
        return ResponseEntity.ok(roomService.updateAdminRoom(idRoom, request));
    }

    @DeleteMapping("/{idRoom}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminRoom(@PathVariable Integer idRoom) {
        roomService.deleteRoom(idRoom);
    }

    @GetMapping("/{idRoom}/reviews")
    public ResponseEntity<List<RoomReviewResponse>> findReviews(@PathVariable Integer idRoom) {
        return ResponseEntity.ok(roomReviewService.findAllByRoomId(idRoom));
    }

    @PatchMapping("{idRoom}/reviews/resolve")
    public ResponseEntity<RoomReviewResponse> resolveReview(@PathVariable Integer idRoom, @RequestBody @Valid RoomReviewDecisionRequest request) {
        return ResponseEntity.ok(roomService.resolveReview(idRoom, request));
    }
}

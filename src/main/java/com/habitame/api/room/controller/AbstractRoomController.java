package com.habitame.api.room.controller;

import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.dto.RoomImageRequest;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.service.RoomReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractRoomController {

    protected final RoomImageService roomImageService;
    protected final RoomService roomService;
    protected final RoomReviewService roomReviewService;


    @GetMapping("/{idRoom}/images")
    public ResponseEntity<List<RoomImageResponse>> findImages(@PathVariable Integer idRoom) {
        return ResponseEntity.ok(roomImageService.findByRoomId(idRoom));
    }

    @PostMapping("/{idRoom}/images")
    public ResponseEntity<RoomImageResponse> addImage(
            @PathVariable Integer idRoom,
            @Valid RoomImageRequest request) throws IOException {
        return ResponseEntity.ok(roomImageService.upload(idRoom, request));
    }

    @DeleteMapping("/images/{idImage}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Integer idImage) throws IOException {
        roomImageService.delete(idImage);
    }

    @PostMapping("/{idRoom}/amenities")
    public ResponseEntity<RoomOwnerResponse> addAmenities(
            @PathVariable Integer idRoom,
            @RequestBody List<Integer> amenities) {
        return ResponseEntity.ok(roomService.addAmenities(idRoom, amenities));
    }

    @DeleteMapping("/{idRoom}/amenities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenities(
            @PathVariable Integer idRoom,
            @RequestBody List<Integer> amenities) {
        roomService.removeAmenities(idRoom, amenities);
    }
}

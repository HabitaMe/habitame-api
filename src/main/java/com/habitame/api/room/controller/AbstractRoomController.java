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


    @GetMapping("/{roomId}/images")
    public ResponseEntity<List<RoomImageResponse>> findImages(@PathVariable Integer roomId) {
        return ResponseEntity.ok(roomImageService.findByRoomId(roomId));
    }

    @PostMapping("/{roomId}/images")
    public ResponseEntity<RoomImageResponse> addImage(
            @PathVariable Integer roomId,
            @Valid RoomImageRequest request) throws IOException {
        return ResponseEntity.ok(roomImageService.upload(roomId, request));
    }

    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Integer id) throws IOException {
        roomImageService.delete(id);
    }

    @PostMapping("/{roomId}/amenities")
    public ResponseEntity<RoomOwnerResponse> addAmenities(
            @PathVariable Integer roomId,
            @RequestBody List<Integer> amenities) {
        return ResponseEntity.ok(roomService.addAmenities(roomId, amenities));
    }

    @DeleteMapping("/{roomId}/amenities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenities(
            @PathVariable Integer roomId,
            @RequestBody List<Integer> amenities) {
        roomService.removeAmenities(roomId, amenities);
    }
}

package com.habitame.api.roomImage.service;

import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.RoomImageMapper;
import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.service.RoomSecurityService;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.dto.RoomImageRequest;
import com.habitame.api.roomImage.dto.RoomImageResponse;
import com.habitame.api.roomImage.entity.RoomImageEntity;
import com.habitame.api.roomImage.repository.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomImageService {

    private final RoomImageRepository roomImageRepository;
    private final ImageStorageService imageStorageService;
    private final RoomService roomService;
    private final RoomSecurityService roomSecurityService;

    @Transactional
    public RoomImageResponse upload(Integer roomId, RoomImageRequest request) throws IOException {
        RoomEntity room = roomService.findEntityById(roomId);

        roomSecurityService.checkRoomAccess(room);

        // Validación del archivo
        if (request.file().isEmpty() || !isImage(request.file())) {
            throw new IllegalArgumentException("Invalid file: must be a non-empty image");
        }

        // Si no hay imagen principal, esta pasa a serlo automáticamente
        boolean main = request.isMain() || roomImageRepository.countMainImages(roomId) == 0;

        if (main) {
            roomImageRepository.resetMainImage(roomId);
        }

        String url = imageStorageService.store(request.file(), "rooms");

        RoomImageEntity roomImageEntity = RoomImageEntity.builder()
                .room(room)
                .imageUrl(url)
                .isMain(request.isMain())
                .build();

        return RoomImageMapper.toResponse(roomImageRepository.save(roomImageEntity));
    }

    @Transactional
    public void delete(Integer imageId) throws IOException {
        RoomImageEntity image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        roomSecurityService.checkImageAccess(image);

        // Separamos el error de storage del error de lógica
        imageStorageService.delete(image.getImageUrl());

        roomImageRepository.deleteById(imageId);
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public List<RoomImageResponse> findByRoomId(Integer roomId) {
        return roomImageRepository.findAllByRoomId(roomId)
                .stream()
                .map(RoomImageMapper::toResponse)
                .toList();
    }

}

package com.habitame.api.room.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.repository.RoomRepository;
import com.habitame.api.roomImage.entity.RoomImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomSecurityService {
    private final RoomRepository roomRepository;

    public void checkRoomAccess(Integer roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        checkRoomAccess(room);
    }

    public void checkRoomAccess(RoomEntity room) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isOwnerOf(room.getProperty().getOwner())) {
            throw new ForbiddenException("Don't have permission to access this room");
        }
    }

    public void checkImageAccess(RoomImageEntity image) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isOwnerOf(image.getRoom().getProperty().getOwner())) {
            throw new ForbiddenException("Don't have permission to access this image");
        }
    }
}

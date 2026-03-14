package com.habitame.api.room.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.repository.RoomRepository;
import com.habitame.api.roomImage.entity.RoomImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomSecurityService {
    private RoomRepository roomRepository;

    /**
     * Verifica que el usuario actual sea el owner de la habitación o un ADMIN.
     * @param propertyId id de la propiedad.
     * @throws ForbiddenException si no tiene permiso.
     */
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

    /**
     * Verifica que el usuario actual sea el owner de la imagen (a través de la room) o un ADMIN.
     */
    public void checkImageAccess(RoomImageEntity image) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isOwnerOf(image.getRoom().getProperty().getOwner())) {
            throw new ForbiddenException("Don't have permission to access this image");
        }
    }
}

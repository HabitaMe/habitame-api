package com.habitame.api.room.service;

import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.mapper.PropertyMapper;
import com.habitame.api.common.mapper.RoomMapper;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.room.dto.RoomPublicDetailResponse;
import com.habitame.api.room.dto.RoomPublicResponse;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import com.habitame.api.room.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public PageResponse<RoomPublicResponse> findAllPublicRooms(Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAllByStatus(RoomStatus.ACTIVE, pageable);

        List<RoomPublicResponse> content = page
                .map(RoomMapper::toPublicResponse)
                .getContent();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public RoomPublicDetailResponse findByIdPublicRoom(Integer idRoom) {
        RoomEntity roomEntity = roomRepository.findByIdAndStatus(idRoom, RoomStatus.ACTIVE).orElseThrow( () -> new ResourceNotFoundException("Room not found: " + idRoom) );
        return RoomMapper.toPublicDetailResponse(roomEntity);
    }
}

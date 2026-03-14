package com.habitame.api.roomImage.repository;

import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.roomImage.entity.RoomImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomImageRepository extends JpaRepository<RoomImageEntity, Integer> {
    @Modifying
    @Query("""
            UPDATE RoomImageEntity r
            SET r.isMain = false
            WHERE r.room.id = :idRoom
            """)
    void resetMainImage(@Param("propertyId") Integer idRoom);

    @Query("SELECT COUNT(r) FROM RoomImageEntity r WHERE r.room.id = :idRoom AND r.isMain = true")
    int countMainImages(@Param("propertyId") Integer idRoom);

    List<RoomImageEntity> findAllByRoomId(Integer idRoom);
}

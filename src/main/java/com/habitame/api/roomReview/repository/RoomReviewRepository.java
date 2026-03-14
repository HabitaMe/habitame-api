package com.habitame.api.roomReview.repository;

import com.habitame.api.roomReview.entity.RoomReviewEntity;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomReviewRepository extends JpaRepository<RoomReviewEntity, Integer> {

    Page<RoomReviewEntity> findAllByStatus(RoomReviewStatus status, Pageable pageable);


    /**
     * Historial completo ordenado de más reciente a más antiguo
     */
    @Query("""
            SELECT r FROM RoomReviewEntity r
            LEFT JOIN FETCH r.admin
            WHERE r.room.id = :roomId
            ORDER BY r.createdAt DESC
            """)
    List<RoomReviewEntity> findAllByRoomId(@Param("roomId") Integer roomId);

    /**
     * La review más reciente para mostrársela al owner si fue rechazada
     */

    @Query("""
            SELECT r FROM RoomReviewEntity r
            LEFT JOIN FETCH r.admin
            WHERE r.room.id = :roomId
            ORDER BY r.createdAt DESC
            LIMIT 1
            """)
    Optional<RoomReviewEntity> findLatestByRoomId(@Param("roomId") Integer roomId);

    /**
     * La review pendiente activa — solo debería haber una a la vez
     */
    Optional<RoomReviewEntity> findByRoomIdAndStatus(Integer roomId, RoomReviewStatus status);
}

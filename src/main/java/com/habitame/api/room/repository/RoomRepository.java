package com.habitame.api.room.repository;

import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Integer>,
        JpaSpecificationExecutor<RoomEntity> {

    @EntityGraph(attributePaths = {"images"})
    Page<RoomEntity> findAll(Specification<RoomEntity> spec, Pageable pageable);
    Page<RoomEntity> findAllByStatus(RoomStatus roomStatus, Pageable pageable);


    @Query("""
                SELECT r FROM RoomEntity r
                LEFT JOIN FETCH r.images i
                WHERE r.id = :id AND r.status = :status
            """)
    Optional<RoomEntity> findByIdAndStatus(Integer id, RoomStatus status);

    Page<RoomEntity> findAllByPropertyId(Integer idProperty, Pageable page);

    Page<RoomEntity> findAllByPropertyOwnerId(Integer ownerId, Pageable pageable);

    Optional<RoomEntity> findByIdAndPropertyOwnerId(Integer roomId, Integer ownerId);

    Page<RoomEntity> findAllByPropertyIdAndStatus(Integer idProperty, RoomStatus status, Pageable pageable);
}

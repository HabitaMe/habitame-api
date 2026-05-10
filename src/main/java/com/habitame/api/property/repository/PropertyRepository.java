package com.habitame.api.property.repository;

import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Integer>,
        JpaSpecificationExecutor<PropertyEntity> {

    @EntityGraph(attributePaths = {"images"})
    Page<PropertyEntity> findAll(Specification<PropertyEntity> spec, Pageable pageable);

    @Query(
            value = "SELECT p FROM PropertyEntity p LEFT JOIN FETCH p.images WHERE p.status = :status",
            countQuery = "SELECT COUNT(p) FROM PropertyEntity p WHERE p.status = :status"
    )
    Page<PropertyEntity> findAllByStatus(@Param("status") PropertyStatus status, Pageable pageable);

    @Query("""
            SELECT p FROM PropertyEntity p
            LEFT JOIN FETCH p.images
            WHERE p.id = :propertyId AND p.status = :status
            """)
    Optional<PropertyEntity> findByIdAndStatus(@Param("propertyId") Integer propertyId,
                                               @Param("status") PropertyStatus status);

    @Query(
            value = "SELECT p FROM PropertyEntity p LEFT JOIN FETCH p.images WHERE p.owner.id = :ownerId",
            countQuery = "SELECT COUNT(p) FROM PropertyEntity p WHERE p.owner.id = :ownerId"
    )
    Page<PropertyEntity> findAllByOwnerId(@Param("ownerId") Integer ownerId, Pageable pageable);

    @Query("""
            SELECT p FROM PropertyEntity p
            LEFT JOIN FETCH p.images
            WHERE p.id = :propertyId AND p.owner.id = :ownerId
            """)
    Optional<PropertyEntity> findByIdAndOwnerId(@Param("ownerId") Integer ownerId,
                                                @Param("propertyId") Integer propertyId);
}

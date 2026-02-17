package com.habitame.api.property.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Integer> {

    @Query("""
        SELECT p FROM PropertyEntity p
        LEFT JOIN FETCH p.images i
        WHERE p.status = :status
    """)
    Page<PropertyEntity> findAllByStatus(@Param("status") PropertyStatus status, Pageable pageable);

    @Query("""
        SELECT p FROM PropertyEntity p
        LEFT JOIN FETCH p.images i
        WHERE p.id = :propertyId AND p.status = :status
    """)
    Optional<PropertyEntity> findByIdAndStatus(Integer propertyId, PropertyStatus status);

    Optional<Page<PropertyEntity>> findAllByOwnerId(Integer ownerId, Pageable pageable);

    @Query("""
        SELECT p FROM PropertyEntity p
        LEFT JOIN FETCH p.images i
        WHERE p.id = :idProperty AND p.owner.id = :ownerId
    """)
    Optional<PropertyEntity> findByIdAndOwnerId(Integer ownerId, Integer idProperty);
}

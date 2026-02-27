package com.habitame.api.propertyReview.repository;

import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.propertyReview.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropertyReviewRepository extends JpaRepository<PropertyReviewEntity, Integer> {
    Page<PropertyReviewEntity> findAllByStatus(ReviewStatus status, Pageable pageable);


    // Historial completo ordenado de más reciente a más antiguo
    @Query("""
        SELECT r FROM PropertyReviewEntity r
        LEFT JOIN FETCH r.admin
        WHERE r.property.id = :propertyId
        ORDER BY r.createdAt DESC
        """)
    List<PropertyReviewEntity> findAllByPropertyId(@Param("propertyId") Integer propertyId);

    // La review más reciente — para mostrársela al owner si fue rechazada
    @Query("""
        SELECT r FROM PropertyReviewEntity r
        LEFT JOIN FETCH r.admin
        WHERE r.property.id = :propertyId
        ORDER BY r.createdAt DESC
        LIMIT 1
        """)
    Optional<PropertyReviewEntity> findLatestByPropertyId(@Param("propertyId") Integer propertyId);

    // La review pendiente activa — solo debería haber una a la vez
    Optional<PropertyReviewEntity> findByPropertyIdAndStatus(Integer propertyId, ReviewStatus status);
}

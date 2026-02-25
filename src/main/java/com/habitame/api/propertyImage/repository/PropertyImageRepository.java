package com.habitame.api.propertyImage.repository;

import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyImageRepository extends JpaRepository<PropertyImageEntity, Integer> {

    @Modifying
    @Query("""
            UPDATE PropertyImageEntity p
            SET p.isMain = false
            WHERE p.property.id = :propertyId
            """)
    void resetMainImage(@Param("propertyId") Integer propertyId);

    @Query("SELECT COUNT(p) FROM PropertyImageEntity p WHERE p.property.id = :propertyId AND p.isMain = true")
    int countMainImages(@Param("propertyId") Integer propertyId);

    List<PropertyImageEntity> findAllByPropertyId(Integer idProperty);
}

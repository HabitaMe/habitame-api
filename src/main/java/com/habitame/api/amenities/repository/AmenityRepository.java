package com.habitame.api.amenities.repository;

import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.entity.AmenityScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AmenityRepository extends JpaRepository<AmenityEntity, Integer> {
    List<AmenityEntity> findAllByScopeIn(Collection<AmenityScope> scopes);
}

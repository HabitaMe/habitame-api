package com.habitame.api.city.repository;

import com.habitame.api.city.entity.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {
    Page<CityEntity> findByProvinceEntity_Id(Integer provinceEntityId, Pageable pageable);

    Page<CityEntity> findAll(Pageable pageable);

    boolean existsByProvinceEntity_IdAndName(Integer provinceId, String name);
}

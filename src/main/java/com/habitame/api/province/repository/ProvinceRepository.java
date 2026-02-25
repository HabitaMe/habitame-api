package com.habitame.api.province.repository;

import com.habitame.api.province.entity.ProvinceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Integer> {
    ProvinceEntity findByName(String name);

    boolean existsByCountryEntity_IdAndName(Integer countryId, String name);

    Page<ProvinceEntity> findByCountryEntity_Id(Integer countryId, Pageable pageable);
}

package com.habitame.api.city.repository;

import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.province.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {
    List<CityEntity> findByProvinceEntity_Id(Integer provinceEntityId);
}

package com.habitame.api.province.repository;

import com.habitame.api.province.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<ProvinceEntity,Integer> {
    ProvinceEntity findByName(String name);
}

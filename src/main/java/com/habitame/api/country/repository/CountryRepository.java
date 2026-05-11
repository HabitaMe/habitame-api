package com.habitame.api.country.repository;

import com.habitame.api.country.entity.CountryEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryEntity, Integer> {

    boolean existsByName(@NotBlank String name);
}

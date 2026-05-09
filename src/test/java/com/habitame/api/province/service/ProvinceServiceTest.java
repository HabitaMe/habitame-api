package com.habitame.api.province.service;

import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.service.CountryService;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.entity.ProvinceEntity;
import com.habitame.api.province.repository.ProvinceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProvinceServiceTest {

    @Mock
    private ProvinceRepository provinceRepository;
    @Mock
    private CountryService countryService;

    @InjectMocks
    private ProvinceService provinceService;

    // ------------------- findAll -------------------

    @Test
    void findAll_ShouldReturnPageResponse() {
        ProvinceEntity entity = buildProvince(1, "Madrid", buildCountry(1, "España"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProvinceEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(provinceRepository.findAll(pageable)).thenReturn(page);

        var result = provinceService.findAll(pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    // ------------------- findEntityById -------------------

    @Test
    void findEntityById_WhenFound_ShouldReturnEntity() {
        ProvinceEntity entity = buildProvince(1, "Madrid", buildCountry(1, "España"));

        when(provinceRepository.findById(1)).thenReturn(Optional.of(entity));

        ProvinceEntity result = provinceService.findEntityById(1);

        assertThat(result.getName()).isEqualTo("Madrid");
    }

    @Test
    void findEntityById_WhenNotFound_ShouldThrow() {
        when(provinceRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provinceService.findEntityById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Province not found: 99");
    }

    // ------------------- findByCountry -------------------

    @Test
    void findByCountry_ShouldReturnFilteredPage() {
        ProvinceEntity entity = buildProvince(1, "Cataluña", buildCountry(1, "España"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProvinceEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(provinceRepository.findByCountryEntity_Id(1, pageable)).thenReturn(page);

        var result = provinceService.findByCountry(1, pageable);

        assertThat(result.content()).hasSize(1);
        verify(provinceRepository).findByCountryEntity_Id(1, pageable);
    }

    // ------------------- addProvince -------------------

    @Test
    void addProvince_WhenNew_ShouldSaveAndReturn() {
        CountryEntity country = buildCountry(1, "España");
        ProvinceRequest request = new ProvinceRequest(1, "Valencia");
        ProvinceEntity saved = buildProvince(3, "Valencia", country);

        when(provinceRepository.existsByCountryEntity_IdAndName(1, "Valencia")).thenReturn(false);
        when(countryService.findEntityById(1)).thenReturn(country);
        when(provinceRepository.save(any())).thenReturn(saved);

        ProvinceResponse result = provinceService.addProvince(request);

        assertThat(result).isNotNull();
        verify(provinceRepository).save(any());
    }

    @Test
    void addProvince_WhenAlreadyExists_ShouldThrowDuplicateException() {
        ProvinceRequest request = new ProvinceRequest(1, "Madrid");

        when(provinceRepository.existsByCountryEntity_IdAndName(1, "Madrid")).thenReturn(true);

        assertThatThrownBy(() -> provinceService.addProvince(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Province already exists");
    }

    // ------------------- updateProvince -------------------

    @Test
    void updateProvince_WhenFound_ShouldUpdateFieldsAndSave() {
        CountryEntity country = buildCountry(1, "España");
        ProvinceEntity entity = buildProvince(1, "OldName", country);
        ProvinceRequest request = new ProvinceRequest(1, "NewName");

        when(provinceRepository.findById(1)).thenReturn(Optional.of(entity));
        when(countryService.findEntityById(1)).thenReturn(country);
        when(provinceRepository.save(any())).thenReturn(entity);

        ProvinceResponse result = provinceService.updateProvince(1, request);

        assertThat(entity.getName()).isEqualTo("NewName");
        verify(provinceRepository).save(entity);
    }

    // ------------------- deleteProvince -------------------

    @Test
    void deleteProvince_WhenFound_ShouldDelete() {
        ProvinceEntity entity = buildProvince(1, "Madrid", buildCountry(1, "España"));

        when(provinceRepository.findById(1)).thenReturn(Optional.of(entity));

        provinceService.deleteProvince(1);

        verify(provinceRepository).delete(entity);
    }

    // ------------------- helpers -------------------

    private CountryEntity buildCountry(Integer id, String name) {
        return CountryEntity.builder()
                .id(id)
                .name(name)
                .isoCode("ESP")
                .build();
    }

    private ProvinceEntity buildProvince(Integer id, String name, CountryEntity country) {
        return ProvinceEntity.builder()
                .id(id)
                .name(name)
                .countryEntity(country)
                .build();
    }
}

package com.habitame.api.city.service;

import com.habitame.api.city.dto.CityRequest;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.city.repository.CityRepository;
import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.province.entity.ProvinceEntity;
import com.habitame.api.province.service.ProvinceService;
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
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private ProvinceService provinceService;

    @InjectMocks
    private CityService cityService;

    // ------------------- findAll -------------------

    @Test
    void findAll_ShouldReturnPageResponse() {
        CityEntity entity = buildCity(1, "Madrid", buildProvince(1, "Madrid"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<CityEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(cityRepository.findAll(pageable)).thenReturn(page);

        var result = cityService.findAll(pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    // ------------------- findEntityById -------------------

    @Test
    void findEntityById_WhenFound_ShouldReturnEntity() {
        CityEntity entity = buildCity(1, "Barcelona", buildProvince(1, "Cataluña"));

        when(cityRepository.findById(1)).thenReturn(Optional.of(entity));

        CityEntity result = cityService.findEntityById(1);

        assertThat(result.getName()).isEqualTo("Barcelona");
    }

    @Test
    void findEntityById_WhenNotFound_ShouldThrow() {
        when(cityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.findEntityById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("City not found: 99");
    }

    // ------------------- findByProvince -------------------

    @Test
    void findByProvince_ShouldReturnFilteredPage() {
        CityEntity entity = buildCity(1, "Zaragoza", buildProvince(2, "Aragón"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<CityEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(cityRepository.findByProvinceEntity_Id(2, pageable)).thenReturn(page);

        var result = cityService.findByProvince(2, pageable);

        assertThat(result.content()).hasSize(1);
        verify(cityRepository).findByProvinceEntity_Id(2, pageable);
    }

    // ------------------- saveCity -------------------

    @Test
    void saveCity_WhenNew_ShouldSaveAndReturn() {
        ProvinceEntity province = buildProvince(1, "Madrid");
        CityRequest request = new CityRequest("Getafe", 1);
        CityEntity saved = buildCity(5, "Getafe", province);

        when(cityRepository.existsByProvinceEntity_IdAndName(1, "Getafe")).thenReturn(false);
        when(provinceService.findEntityById(1)).thenReturn(province);
        when(cityRepository.save(any())).thenReturn(saved);

        CityResponse result = cityService.saveCity(request);

        assertThat(result).isNotNull();
        verify(cityRepository).save(any());
    }

    @Test
    void saveCity_WhenAlreadyExists_ShouldThrowDuplicateException() {
        CityRequest request = new CityRequest("Madrid", 1);

        when(cityRepository.existsByProvinceEntity_IdAndName(1, "Madrid")).thenReturn(true);

        assertThatThrownBy(() -> cityService.saveCity(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("City already exists");
    }

    // ------------------- updateCity -------------------

    @Test
    void updateCity_WhenFound_ShouldUpdateNameAndSave() {
        ProvinceEntity province = buildProvince(1, "Madrid");
        CityEntity entity = buildCity(1, "OldName", province);
        CityRequest request = new CityRequest("NewName", null);

        when(cityRepository.findById(1)).thenReturn(Optional.of(entity));
        when(cityRepository.save(any())).thenReturn(entity);

        CityResponse result = cityService.updateCity(1, request);

        assertThat(entity.getName()).isEqualTo("NewName");
        verify(cityRepository).save(entity);
    }

    @Test
    void updateCity_WhenProvinceIdProvided_ShouldUpdateProvince() {
        ProvinceEntity oldProvince = buildProvince(1, "Madrid");
        ProvinceEntity newProvince = buildProvince(2, "Cataluña");
        CityEntity entity = buildCity(1, "Alcalá", oldProvince);
        CityRequest request = new CityRequest("Alcalá", 2);

        when(cityRepository.findById(1)).thenReturn(Optional.of(entity));
        when(provinceService.findEntityById(2)).thenReturn(newProvince);
        when(cityRepository.save(any())).thenReturn(entity);

        cityService.updateCity(1, request);

        assertThat(entity.getProvinceEntity()).isEqualTo(newProvince);
        verify(provinceService).findEntityById(2);
    }

    // ------------------- deleteCity -------------------

    @Test
    void deleteCity_WhenFound_ShouldDelete() {
        ProvinceEntity province = buildProvince(1, "Madrid");
        CityEntity entity = buildCity(1, "Leganés", province);

        when(cityRepository.findById(1)).thenReturn(Optional.of(entity));

        cityService.deleteCity(1);

        verify(cityRepository).delete(entity);
    }

    // ------------------- helpers -------------------

    private ProvinceEntity buildProvince(Integer id, String name) {
        return ProvinceEntity.builder()
                .id(id)
                .name(name)
                .build();
    }

    private CityEntity buildCity(Integer id, String name, ProvinceEntity province) {
        return CityEntity.builder()
                .id(id)
                .name(name)
                .provinceEntity(province)
                .build();
    }
}

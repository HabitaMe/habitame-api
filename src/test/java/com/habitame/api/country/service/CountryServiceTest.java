package com.habitame.api.country.service;

import com.habitame.api.common.exception.DuplicateResourceException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.entity.CountryEntity;
import com.habitame.api.country.repository.CountryRepository;
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
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;


    @Test
    void findAll_ShouldReturnPageResponse() {
        CountryEntity entity = buildCountry(1, "España", "ESP");
        Pageable pageable = PageRequest.of(0, 10);
        Page<CountryEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(countryRepository.findAll(pageable)).thenReturn(page);

        var result = countryService.findAll(pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }


    @Test
    void findById_WhenFound_ShouldReturnResponse() {
        CountryEntity entity = buildCountry(1, "España", "ESP");

        when(countryRepository.findById(1)).thenReturn(Optional.of(entity));

        CountryResponse result = countryService.findById(1);

        assertThat(result).isNotNull();
    }

    @Test
    void findEntityById_WhenNotFound_ShouldThrow() {
        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.findEntityById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Country not found: 99");
    }


    @Test
    void addCountry_WhenNameIsNew_ShouldSaveAndReturn() {
        CountryRequest request = new CountryRequest("Francia", "FRA");
        CountryEntity saved = buildCountry(2, "Francia", "FRA");

        when(countryRepository.existsByName("Francia")).thenReturn(false);
        when(countryRepository.save(any())).thenReturn(saved);

        CountryResponse result = countryService.addCountry(request);

        assertThat(result).isNotNull();
        verify(countryRepository).save(any());
    }

    @Test
    void addCountry_WhenNameExists_ShouldThrowDuplicateException() {
        CountryRequest request = new CountryRequest("España", "ESP");

        when(countryRepository.existsByName("España")).thenReturn(true);

        assertThatThrownBy(() -> countryService.addCountry(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("España");
    }


    @Test
    void updateCountry_WhenFound_ShouldUpdateFields() {
        CountryEntity entity = buildCountry(1, "España", "ESP");
        CountryRequest request = new CountryRequest("Spain", "SP");

        when(countryRepository.findById(1)).thenReturn(Optional.of(entity));
        when(countryRepository.save(any())).thenReturn(entity);

        CountryResponse result = countryService.updateCountry(1, request);

        assertThat(entity.getName()).isEqualTo("Spain");
        assertThat(entity.getIsoCode()).isEqualTo("SP");
        verify(countryRepository).save(entity);
    }

    @Test
    void updateCountry_WhenNotFound_ShouldThrow() {
        CountryRequest request = new CountryRequest("Alemania", "DEU");

        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.updateCountry(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void deleteCountry_WhenFound_ShouldDelete() {
        CountryEntity entity = buildCountry(1, "España", "ESP");

        when(countryRepository.findById(1)).thenReturn(Optional.of(entity));

        countryService.deleteCountry(1);

        verify(countryRepository).delete(entity);
    }


    private CountryEntity buildCountry(Integer id, String name, String isoCode) {
        return CountryEntity.builder()
                .id(id)
                .name(name)
                .isoCode(isoCode)
                .build();
    }
}

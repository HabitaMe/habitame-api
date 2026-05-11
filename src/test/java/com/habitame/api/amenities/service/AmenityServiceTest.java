package com.habitame.api.amenities.service;

import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.repository.AmenityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityService amenityService;


    @Test
    void findAmenities_ShouldReturnMappedList() {
        AmenityEntity entity = buildAmenity(1, "WiFi", AmenityScope.BOTH);

        when(amenityRepository.findAll()).thenReturn(List.of(entity));

        List<AmenityResponse> result = amenityService.findAmenities();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("WiFi");
    }


    @Test
    void findAmenitiesByScope_ShouldQueryWithScopeAndBoth() {
        AmenityEntity entity = buildAmenity(2, "Parking", AmenityScope.PROPERTY);

        when(amenityRepository.findAllByScopeIn(List.of(AmenityScope.PROPERTY, AmenityScope.BOTH)))
                .thenReturn(List.of(entity));

        List<AmenityResponse> result = amenityService.findAmenitiesByScope(AmenityScope.PROPERTY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Parking");
        verify(amenityRepository).findAllByScopeIn(List.of(AmenityScope.PROPERTY, AmenityScope.BOTH));
    }


    @Test
    void findAmenityById_WhenFound_ShouldReturnEntity() {
        AmenityEntity entity = buildAmenity(3, "Pool", AmenityScope.PROPERTY);

        when(amenityRepository.findById(3)).thenReturn(Optional.of(entity));

        AmenityEntity result = amenityService.findAmenityById(3);

        assertThat(result.getName()).isEqualTo("Pool");
    }

    @Test
    void findAmenityById_WhenNotFound_ShouldThrow() {
        when(amenityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amenityService.findAmenityById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amenity not found: 99");
    }


    @Test
    void saveAmenity_ShouldPersistAndReturnMappedResponse() {
        AmenityRequest request = new AmenityRequest("Gym", "Fitness room", AmenityScope.PROPERTY);
        AmenityEntity saved = buildAmenity(4, "Gym", AmenityScope.PROPERTY);

        when(amenityRepository.save(any())).thenReturn(saved);

        AmenityResponse result = amenityService.saveAmenity(request);

        assertThat(result.name()).isEqualTo("Gym");
        assertThat(result.id()).isEqualTo(4);
        verify(amenityRepository).save(any());
    }


    @Test
    void updateAmenity_WhenFound_ShouldUpdateAndReturn() {
        AmenityEntity existing = buildAmenity(5, "OldName", AmenityScope.ROOM);
        AmenityRequest request = new AmenityRequest("NewName", "New description", AmenityScope.BOTH);
        AmenityEntity updated = buildAmenity(5, "NewName", AmenityScope.BOTH);

        when(amenityRepository.findById(5)).thenReturn(Optional.of(existing));
        when(amenityRepository.save(any())).thenReturn(updated);

        AmenityResponse result = amenityService.updateAmenity(5, request);

        assertThat(result.name()).isEqualTo("NewName");
        verify(amenityRepository).save(any());
    }

    @Test
    void updateAmenity_WhenNotFound_ShouldThrow() {
        AmenityRequest request = new AmenityRequest("Name", "Desc", AmenityScope.BOTH);

        when(amenityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amenityService.updateAmenity(99, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amenity not found: 99");
    }


    @Test
    void deleteAmenity_ShouldCallDeleteById() {
        amenityService.deleteAmenity(7);

        verify(amenityRepository).deleteById(7);
    }


    private AmenityEntity buildAmenity(Integer id, String name, AmenityScope scope) {
        return AmenityEntity.builder()
                .id(id)
                .name(name)
                .description("A description")
                .scope(scope)
                .build();
    }
}

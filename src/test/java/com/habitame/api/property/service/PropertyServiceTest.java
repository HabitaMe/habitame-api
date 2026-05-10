package com.habitame.api.property.service;

import com.habitame.api.amenities.entity.AmenityEntity;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminDetailResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.dto.PropertyOwnerDetailResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.dto.PropertyFilter;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.repository.PropertyRepository;
import org.springframework.data.jpa.domain.Specification;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private CityService cityService;
    @Mock
    private PropertyReviewService propertyReviewService;
    @Mock
    private UserService userService;
    @Mock
    private AmenityService amenityService;
    @Mock
    private PropertySecurityService propertySecurityService;

    @InjectMocks
    private PropertyService propertyService;

    private MockedStatic<SecurityUtils> securityUtils;
    private UserEntity owner;
    private CityEntity city;

    @BeforeEach
    void setUp() {
        owner = new UserEntity();
        owner.setId(1);
        owner.setUsername("juanito");
        owner.setRole(Role.ARRENDADOR);

        city = new CityEntity();
        city.setId(10);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(owner);
        securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }


    @Test
    void updateOwnerProperty_WhenTitleChanges_ShouldTriggerReview() {
        PropertyEntity property = buildProperty("Piso en Madrid", "Descripción original", "Calle Mayor 1");
        PropertyOwnerRequest request = buildRequest("Piso reformado en Madrid", "Descripción original", "Calle Mayor 1");

        when(propertyRepository.findByIdAndOwnerId(1, 1)).thenReturn(Optional.of(property));
        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.updateOwnerProperty(1, request);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.IN_REVIEW);
        verify(propertyReviewService).addReview(property);
    }

    @Test
    void updateOwnerProperty_WhenAddressChanges_ShouldTriggerReview() {
        PropertyEntity property = buildProperty("Piso en Madrid", "Descripción", "Calle Mayor 1");
        PropertyOwnerRequest request = buildRequest("Piso en Madrid", "Descripción", "Gran Vía 10");

        when(propertyRepository.findByIdAndOwnerId(1, 1)).thenReturn(Optional.of(property));
        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.updateOwnerProperty(1, request);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.IN_REVIEW);
        verify(propertyReviewService).addReview(property);
    }

    @Test
    void updateOwnerProperty_WhenOnlyFloorChanges_ShouldNotTriggerReview() {
        PropertyEntity property = buildProperty("Piso en Madrid", "Descripción", "Calle Mayor 1");
        property.setStatus(PropertyStatus.ACTIVE);

        // mismo título, descripción y dirección — solo cambia el floor en el request
        PropertyOwnerRequest request = new PropertyOwnerRequest(
                "Piso en Madrid", "Descripción", "apartamento", "Calle Mayor 1",
                10, 3, BigDecimal.valueOf(60), 1, false
        );

        when(propertyRepository.findByIdAndOwnerId(1, 1)).thenReturn(Optional.of(property));
        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.updateOwnerProperty(1, request);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.ACTIVE);
        verify(propertyReviewService, never()).addReview(any());
    }

    @Test
    void updateOwnerProperty_WhenPropertyIsInactive_ShouldAlwaysTriggerReview() {
        PropertyEntity property = buildProperty("Piso en Madrid", "Descripción", "Calle Mayor 1");
        property.setStatus(PropertyStatus.INACTIVE);

        PropertyOwnerRequest request = buildRequest("Piso en Madrid", "Descripción", "Calle Mayor 1");

        when(propertyRepository.findByIdAndOwnerId(1, 1)).thenReturn(Optional.of(property));
        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.updateOwnerProperty(1, request);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.IN_REVIEW);
        verify(propertyReviewService).addReview(property);
    }

    @Test
    void updateOwnerProperty_WhenNotFound_ShouldThrow() {
        PropertyOwnerRequest request = buildRequest("Título", "Descripción", "Dirección");

        when(propertyRepository.findByIdAndOwnerId(1, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.updateOwnerProperty(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void resolveReview_WhenApproved_ShouldSetPropertyActive() {
        PropertyEntity property = buildProperty("Piso en Madrid", "Descripción", "Calle Mayor 1");
        property.setStatus(PropertyStatus.IN_REVIEW);

        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(PropertyReviewStatus.APPROVED, null);
        PropertyReviewResponse reviewResponse = new PropertyReviewResponse(1, "APPROVED", 1);

        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));
        when(propertyReviewService.resolveReview(1, request)).thenReturn(reviewResponse);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.resolveReview(1, request);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.ACTIVE);
    }

    @Test
    void resolveReview_WhenRejected_ShouldSetPropertyInactive() {
        PropertyEntity property = buildProperty("Piso en Madrid", "Descripción", "Calle Mayor 1");
        property.setStatus(PropertyStatus.IN_REVIEW);

        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(
                PropertyReviewStatus.REJECTED, "La descripción es insuficiente"
        );
        PropertyReviewResponse reviewResponse = new PropertyReviewResponse(1, "REJECTED", 1);

        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));
        when(propertyReviewService.resolveReview(1, request)).thenReturn(reviewResponse);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.resolveReview(1, request);

        assertThat(property.getStatus()).isEqualTo(PropertyStatus.INACTIVE);
    }


    @Test
    void findPublicProperties_ShouldReturnMappedPage() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        property.setStatus(PropertyStatus.ACTIVE);
        when(propertyRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(property)));

        PageResponse<PropertyPublicResponse> result = propertyService.findPublicProperties(
                new PropertyFilter(null, null), PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findPublicPropertyById_WhenFound_ShouldReturn() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        when(propertyRepository.findByIdAndStatus(1, PropertyStatus.ACTIVE)).thenReturn(Optional.of(property));

        PropertyPublicDetailResponse result = propertyService.findPublicPropertyById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findPublicPropertyById_WhenNotFound_ShouldThrow() {
        when(propertyRepository.findByIdAndStatus(99, PropertyStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.findPublicPropertyById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void findAllByOwner_ShouldReturnPage() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        when(propertyRepository.findAllByOwnerId(1, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(property)));

        PageResponse<PropertyOwnerResponse> result = propertyService.findAllByOwner(PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findMyPropertyById_WhenFound_ShouldReturn() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        when(propertyRepository.findByIdAndOwnerId(1, 1)).thenReturn(Optional.of(property));

        PropertyOwnerDetailResponse result = propertyService.findMyPropertyById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findMyPropertyById_WhenNotFound_ShouldThrow() {
        when(propertyRepository.findByIdAndOwnerId(1, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.findMyPropertyById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void addOwnerProperty_ShouldSaveAndTriggerReview() {
        PropertyOwnerRequest request = buildRequest("Piso nuevo", "Desc", "Calle Nueva");

        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PropertyOwnerResponse result = propertyService.addOwnerProperty(request);

        assertThat(result).isNotNull();
        verify(propertyReviewService).addReview(any());
    }


    @Test
    void findAll_ShouldReturnAllProperties() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        when(propertyRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(property)));

        PageResponse<PropertyAdminResponse> result = propertyService.findAll(PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findById_WhenFound_ShouldReturnAdminDetail() {
        PropertyEntity property = buildPropertyWithTimestamps("Piso", "Desc", "Calle");
        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));

        PropertyAdminDetailResponse result = propertyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        when(propertyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void saveAdminProperty_ShouldSaveAndReturn() {
        PropertyAdminRequest request = new PropertyAdminRequest(
                "Piso", "Desc", "apartamento", "Calle", 10, 1, BigDecimal.valueOf(60), 2, false, 1
        );
        PropertyEntity saved = buildProperty("Piso", "Desc", "Calle");

        when(userService.findById(1)).thenReturn(owner);
        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenReturn(saved);

        PropertyAdminResponse result = propertyService.saveAdminProperty(request);

        assertThat(result.id()).isEqualTo(1);
    }


    @Test
    void updateAdminProperty_WhenFound_ShouldUpdate() {
        PropertyAdminRequest request = new PropertyAdminRequest(
                "Piso", "Desc", "apartamento", "Calle", 10, 1, BigDecimal.valueOf(60), 2, false, 1
        );
        PropertyEntity property = buildPropertyWithTimestamps("Piso", "Desc", "Calle");

        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));
        when(userService.findById(1)).thenReturn(owner);
        when(cityService.findEntityById(10)).thenReturn(city);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PropertyAdminDetailResponse result = propertyService.updateAdminProperty(1, request);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void updateAdminProperty_WhenNotFound_ShouldThrow() {
        PropertyAdminRequest request = new PropertyAdminRequest(
                "Piso", "Desc", "apartamento", "Calle", 10, 1, BigDecimal.valueOf(60), 2, false, 1
        );
        when(propertyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.updateAdminProperty(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void deleteProperty_ShouldDeleteProperty() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));

        propertyService.deleteProperty(1);

        verify(propertySecurityService).checkPropertyAccess(property);
        verify(propertyRepository).delete(property);
    }

    @Test
    void deleteProperty_WhenNotFound_ShouldThrow() {
        when(propertyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.deleteProperty(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void addAmenities_ShouldAddToPropertyAndReturn() {
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        AmenityEntity amenity = AmenityEntity.builder()
                .id(1).name("WiFi").description("desc").scope(AmenityScope.PROPERTY).build();

        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));
        when(amenityService.findAmenityById(1)).thenReturn(amenity);
        when(propertyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyService.addAmenities(1, List.of(1));

        assertThat(property.getPropertyAmenities()).contains(amenity);
    }


    @Test
    void removeAmenities_ShouldRemoveFromProperty() {
        AmenityEntity amenity = AmenityEntity.builder()
                .id(1).name("WiFi").description("desc").scope(AmenityScope.PROPERTY).build();
        PropertyEntity property = buildProperty("Piso", "Desc", "Calle");
        property.getPropertyAmenities().add(amenity);

        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));
        when(amenityService.findAmenityById(1)).thenReturn(amenity);

        propertyService.removeAmenities(1, List.of(1));

        assertThat(property.getPropertyAmenities()).doesNotContain(amenity);
    }


    private PropertyEntity buildPropertyWithTimestamps(String title, String description, String address) {
        return PropertyEntity.builder()
                .id(1)
                .title(title)
                .description(description)
                .address(address)
                .type("apartamento")
                .floor(1)
                .areaM2(BigDecimal.valueOf(60))
                .bathroomsTotal(1)
                .ownerInHouse(false)
                .status(PropertyStatus.IN_REVIEW)
                .owner(owner)
                .cityEntity(city)
                .createdAt(LocalDateTime.now())
                .images(new ArrayList<>())
                .propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();
    }

    private PropertyEntity buildProperty(String title, String description, String address) {
        return PropertyEntity.builder()
                .id(1)
                .title(title)
                .description(description)
                .address(address)
                .type("apartamento")
                .floor(1)
                .areaM2(BigDecimal.valueOf(60))
                .bathroomsTotal(1)
                .ownerInHouse(false)
                .status(PropertyStatus.IN_REVIEW)
                .owner(owner)
                .cityEntity(city)
                .images(new ArrayList<>())
                .propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();
    }

    private PropertyOwnerRequest buildRequest(String title, String description, String address) {
        return new PropertyOwnerRequest(
                title, description, "apartamento", address, 10, 1, BigDecimal.valueOf(60), 1, false
        );
    }
}

package com.habitame.api.property.service;

import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.repository.PropertyRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
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

    // ------------------- updateOwnerProperty -------------------

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

        when(propertyRepository.findByIdAndOwnerId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.updateOwnerProperty(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ------------------- resolveReview -------------------

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

    // ------------------- helpers -------------------

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

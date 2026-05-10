package com.habitame.api.propertyReview.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.city.entity.CityEntity;
import com.habitame.api.common.exception.IllegalArgument;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.service.PropertySecurityService;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
import com.habitame.api.propertyReview.dto.PropertyReviewDetailResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewEntity;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.propertyReview.repository.PropertyReviewRepository;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyReviewServiceTest {

    @Mock
    private PropertyReviewRepository propertyReviewRepository;
    @Mock
    private PropertySecurityService propertySecurityService;

    @InjectMocks
    private PropertyReviewService propertyReviewService;

    private MockedStatic<SecurityUtils> securityUtils;
    private UserEntity admin;

    @BeforeEach
    void setUp() {
        admin = new UserEntity();
        admin.setId(99);
        admin.setRole(Role.ADMIN);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(admin);
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }


    @Test
    void resolveReview_WhenApproved_ShouldUpdateReviewStatus() {
        PropertyReviewEntity review = buildPendingReview();
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(PropertyReviewStatus.APPROVED, null);

        when(propertyReviewRepository.findByPropertyIdAndStatus(1, PropertyReviewStatus.PENDING))
                .thenReturn(Optional.of(review));
        when(propertyReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PropertyReviewResponse response = propertyReviewService.resolveReview(1, request);

        assertThat(review.getStatus()).isEqualTo(PropertyReviewStatus.APPROVED);
        assertThat(review.getAdmin()).isEqualTo(admin);
        assertThat(review.getReviewedAt()).isNotNull();
    }

    @Test
    void resolveReview_WhenRejectedWithComment_ShouldUpdateReview() {
        PropertyReviewEntity review = buildPendingReview();
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(
                PropertyReviewStatus.REJECTED, "Imágenes de baja calidad"
        );

        when(propertyReviewRepository.findByPropertyIdAndStatus(1, PropertyReviewStatus.PENDING))
                .thenReturn(Optional.of(review));
        when(propertyReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyReviewService.resolveReview(1, request);

        assertThat(review.getStatus()).isEqualTo(PropertyReviewStatus.REJECTED);
        assertThat(review.getComment()).isEqualTo("Imágenes de baja calidad");
    }

    @Test
    void resolveReview_WhenRejectedWithoutComment_ShouldThrow() {
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(
                PropertyReviewStatus.REJECTED, null
        );

        assertThatThrownBy(() -> propertyReviewService.resolveReview(1, request))
                .isInstanceOf(IllegalArgument.class)
                .hasMessageContaining("Comment is required");

        verify(propertyReviewRepository, never()).save(any());
    }

    @Test
    void resolveReview_WhenRejectedWithBlankComment_ShouldThrow() {
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(
                PropertyReviewStatus.REJECTED, "   "
        );

        assertThatThrownBy(() -> propertyReviewService.resolveReview(1, request))
                .isInstanceOf(IllegalArgument.class);
    }

    @Test
    void resolveReview_WhenNoPendingReview_ShouldThrow() {
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(PropertyReviewStatus.APPROVED, null);

        when(propertyReviewRepository.findByPropertyIdAndStatus(99, PropertyReviewStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyReviewService.resolveReview(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void getReviews_ShouldReturnMappedPage() {
        PropertyReviewEntity review = buildPendingReview();
        when(propertyReviewRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(review)));

        PageResponse<PropertyReviewResponse> result = propertyReviewService.getReviews(PageRequest.of(0, 10));

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findById_WhenFound_ShouldReturnDetail() {
        PropertyReviewEntity review = buildPendingReviewWithCity();
        when(propertyReviewRepository.findById(1)).thenReturn(Optional.of(review));

        PropertyReviewDetailResponse result = propertyReviewService.findById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        when(propertyReviewRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyReviewService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void getReviewsByStatus_ShouldReturnFilteredPage() {
        PropertyReviewEntity review = buildPendingReview();
        when(propertyReviewRepository.findAllByStatus(PropertyReviewStatus.PENDING, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(review)));

        PageResponse<PropertyReviewResponse> result = propertyReviewService.getReviewsByStatus(
                PropertyReviewStatus.PENDING, PageRequest.of(0, 10)
        );

        assertThat(result.content()).hasSize(1);
    }


    @Test
    void findAllByPropertyId_ShouldReturnList() {
        PropertyReviewEntity review = buildPendingReview();
        when(propertyReviewRepository.findAllByPropertyId(1)).thenReturn(List.of(review));

        List<PropertyReviewResponse> result = propertyReviewService.findAllByPropertyId(1);

        assertThat(result).hasSize(1);
    }


    @Test
    void findLatestRejectedReview_WhenRejectedExists_ShouldReturnIt() {
        PropertyReviewEntity review = buildPendingReviewWithCity();
        review.setStatus(PropertyReviewStatus.REJECTED);
        review.setComment("Fotos insuficientes");

        when(propertyReviewRepository.findLatestByPropertyId(1)).thenReturn(Optional.of(review));

        Optional<PropertyReviewDetailResponse> result = propertyReviewService.findLatestRejectedReview(1);

        assertThat(result).isPresent();
    }

    @Test
    void findLatestRejectedReview_WhenPending_ShouldReturnEmpty() {
        PropertyReviewEntity review = buildPendingReview();

        when(propertyReviewRepository.findLatestByPropertyId(1)).thenReturn(Optional.of(review));

        Optional<PropertyReviewDetailResponse> result = propertyReviewService.findLatestRejectedReview(1);

        assertThat(result).isEmpty();
    }

    @Test
    void findLatestRejectedReview_WhenNone_ShouldReturnEmpty() {
        when(propertyReviewRepository.findLatestByPropertyId(1)).thenReturn(Optional.empty());

        Optional<PropertyReviewDetailResponse> result = propertyReviewService.findLatestRejectedReview(1);

        assertThat(result).isEmpty();
    }


    @Test
    void addReview_ShouldCreatePendingReview() {
        PropertyEntity property = PropertyEntity.builder()
                .id(1).title("Piso").description("Desc").address("Calle")
                .status(PropertyStatus.IN_REVIEW)
                .images(new ArrayList<>()).propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>()).rooms(new ArrayList<>())
                .build();

        when(propertyReviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        propertyReviewService.addReview(property);

        verify(propertyReviewRepository).save(argThat(review ->
                review.getProperty().equals(property)
                        && review.getStatus() == PropertyReviewStatus.PENDING
        ));
    }


    private PropertyReviewEntity buildPendingReview() {
        PropertyEntity property = PropertyEntity.builder()
                .id(1).title("Piso").description("Desc").address("Calle")
                .status(PropertyStatus.IN_REVIEW)
                .images(new ArrayList<>()).propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>()).rooms(new ArrayList<>())
                .build();

        PropertyReviewEntity review = new PropertyReviewEntity();
        review.setId(1);
        review.setStatus(PropertyReviewStatus.PENDING);
        review.setProperty(property);
        return review;
    }

    private PropertyReviewEntity buildPendingReviewWithCity() {
        CityEntity city = new CityEntity();
        city.setId(10);
        city.setName("Madrid");

        PropertyEntity property = PropertyEntity.builder()
                .id(1).title("Piso").description("Desc").address("Calle")
                .areaM2(BigDecimal.valueOf(60)).bathroomsTotal(1).floor(1)
                .status(PropertyStatus.IN_REVIEW)
                .cityEntity(city)
                .images(new ArrayList<>()).propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>()).rooms(new ArrayList<>())
                .build();

        PropertyReviewEntity review = new PropertyReviewEntity();
        review.setId(1);
        review.setStatus(PropertyReviewStatus.PENDING);
        review.setProperty(property);
        return review;
    }
}

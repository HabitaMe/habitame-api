package com.habitame.api.propertyReview.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.IllegalArgument;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.service.PropertySecurityService;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
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

import java.util.ArrayList;
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

    // ------------------- resolveReview -------------------

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

    // ------------------- addReview -------------------

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

    // ------------------- helpers -------------------

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
}

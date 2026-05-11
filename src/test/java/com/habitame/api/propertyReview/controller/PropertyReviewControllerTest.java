package com.habitame.api.propertyReview.controller;

import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.propertyReview.dto.PropertyReviewResponse;
import com.habitame.api.propertyReview.entity.PropertyReviewStatus;
import com.habitame.api.propertyReview.service.PropertyReviewService;
import com.habitame.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PropertyReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyReviewService propertyReviewService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getReviews_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/property-reviews"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void getReviews_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/v1/property-reviews"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReviews_ShouldReturnPage() throws Exception {
        PageResponse<PropertyReviewResponse> page = new PageResponse<>(
                List.of(new PropertyReviewResponse(1, "PENDING", 10)),
                0, 10, 1, 1
        );
        when(propertyReviewService.getReviews(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/v1/property-reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].Status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReviewsByStatus_ShouldReturnFilteredPage() throws Exception {
        PageResponse<PropertyReviewResponse> page = new PageResponse<>(
                List.of(new PropertyReviewResponse(2, "REJECTED", 5)),
                0, 10, 1, 1
        );
        when(propertyReviewService.getReviewsByStatus(eq(PropertyReviewStatus.REJECTED), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/v1/property-reviews/status/REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].Status").value("REJECTED"));
    }
}

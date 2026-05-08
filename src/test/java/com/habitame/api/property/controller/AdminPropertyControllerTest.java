package com.habitame.api.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyAdminRequest;
import com.habitame.api.property.dto.PropertyAdminResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
import com.habitame.api.propertyReview.dto.PropertyReviewDecisionRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminPropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private PropertyImageService propertyImageService;

    @MockBean
    private PropertyReviewService propertyReviewService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    // ------------------- GET /api/admin/properties -------------------

    @Test
    void findAll_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/properties"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void findAll_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/properties"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_ShouldReturnPage() throws Exception {
        when(propertyService.findAll(any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));

        mockMvc.perform(get("/api/admin/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ------------------- POST /api/admin/properties -------------------

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void saveProperty_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/admin/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveProperty_WithMissingTitle_ShouldReturn400() throws Exception {
        PropertyAdminRequest request = new PropertyAdminRequest(
                "", "Descripción", "apartamento", "Calle Mayor", 1, 1, BigDecimal.valueOf(60), 1, false, 1
        );

        mockMvc.perform(post("/api/admin/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveProperty_ShouldReturnCreated() throws Exception {
        PropertyAdminResponse response = new PropertyAdminResponse(42, "Piso", null, "ACTIVE", null);
        when(propertyService.saveAdminProperty(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "api/admin/properties/42"));
    }

    // ------------------- DELETE /api/admin/properties/{id} -------------------

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteProperty_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/admin/properties/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProperty_ShouldReturnNoContent() throws Exception {
        doNothing().when(propertyService).deleteProperty(1);

        mockMvc.perform(delete("/api/admin/properties/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProperty_WhenNotFound_ShouldReturn404() throws Exception {
        when(propertyService.findById(99)).thenThrow(new ResourceNotFoundException("Property not found: 99"));

        mockMvc.perform(get("/api/admin/properties/99"))
                .andExpect(status().isNotFound());
    }

    // ------------------- PATCH /api/admin/properties/{id}/reviews/resolve -------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void resolveReview_Approved_ShouldReturn200() throws Exception {
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(PropertyReviewStatus.APPROVED, null);
        PropertyReviewResponse response = new PropertyReviewResponse(1, "APPROVED", 1);

        when(propertyService.resolveReview(eq(1), any())).thenReturn(response);

        mockMvc.perform(patch("/api/admin/properties/1/reviews/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void resolveReview_WithWrongRole_ShouldReturn403() throws Exception {
        PropertyReviewDecisionRequest request = new PropertyReviewDecisionRequest(PropertyReviewStatus.APPROVED, null);

        mockMvc.perform(patch("/api/admin/properties/1/reviews/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private PropertyAdminRequest buildValidRequest() {
        return new PropertyAdminRequest(
                "Piso en Madrid", "Descripción", "apartamento",
                "Calle Mayor 1", 1, 2, BigDecimal.valueOf(75), 1, false, 5
        );
    }
}

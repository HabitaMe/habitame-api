package com.habitame.api.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyOwnerRequest;
import com.habitame.api.property.dto.PropertyOwnerResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.service.PropertyImageService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OwnerPropertyControllerTest {

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


    @Test
    void findMyProperties_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/owner/properties"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findMyProperties_WithAdminRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/v1/owner/properties"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void findMyProperties_ShouldReturnPage() throws Exception {
        PageResponse<PropertyOwnerResponse> page = new PageResponse<>(List.of(), 0, 10, 0, 0);
        when(propertyService.findAllByOwner(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/v1/owner/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }


    @Test
    void addOwnerProperty_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/owner/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addOwnerProperty_WithAdminRole_ShouldReturn403() throws Exception {
        PropertyOwnerRequest request = buildValidRequest();

        mockMvc.perform(post("/v1/owner/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addOwnerProperty_WithMissingTitle_ShouldReturn400() throws Exception {
        PropertyOwnerRequest request = new PropertyOwnerRequest(
                "", "Descripción válida", "apartamento", "Calle Mayor 1",
                1, 1, BigDecimal.valueOf(60), 1, false
        );

        mockMvc.perform(post("/v1/owner/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addOwnerProperty_WithMissingCityId_ShouldReturn400() throws Exception {
        PropertyOwnerRequest request = new PropertyOwnerRequest(
                "Piso en Madrid", "Descripción válida", "apartamento", "Calle Mayor 1",
                null, 1, BigDecimal.valueOf(60), 1, false
        );

        mockMvc.perform(post("/v1/owner/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addOwnerProperty_ShouldReturnCreated() throws Exception {
        PropertyOwnerRequest request = buildValidRequest();
        PropertyOwnerResponse response = new PropertyOwnerResponse(
                42, "Piso en Madrid", "Calle Mayor 1", null, BigDecimal.valueOf(60), 1, 1, null, "IN_REVIEW"
        );

        when(propertyService.addOwnerProperty(any(PropertyOwnerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/owner/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "v1/owner/properties/42"));
    }


    @Test
    void deleteOwnerProperty_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/v1/owner/properties/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDATARIO")
    void deleteOwnerProperty_WithArrendatarioRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/v1/owner/properties/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteOwnerProperty_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/v1/owner/properties/1"))
                .andExpect(status().isNoContent());
    }


    private PropertyOwnerRequest buildValidRequest() {
        return new PropertyOwnerRequest(
                "Piso en Madrid", "Descripción del piso", "apartamento",
                "Calle Mayor 1", 1, 2, BigDecimal.valueOf(75), 1, false
        );
    }
}

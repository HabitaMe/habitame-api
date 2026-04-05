package com.habitame.api.province.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.province.dto.ProvinceRequest;
import com.habitame.api.province.dto.ProvinceResponse;
import com.habitame.api.province.service.ProvinceService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProvinceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProvinceService provinceService;

    @MockBean
    private CityService cityService;

    @Autowired
    private ObjectMapper objectMapper;

    // ------------------- GET /api/provinces -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findAll_ShouldReturnProvinces() throws Exception {
        PageResponse<ProvinceResponse> mockPage = new PageResponse<>(
                List.of(new ProvinceResponse(1, "Madrid"), new ProvinceResponse(2, "Barcelona")),
                0, 2, 1, 2
        );

        when(provinceService.findAll(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/provinces")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Madrid"))
                .andExpect(jsonPath("$.content[1].name").value("Barcelona"));
    }

    @Test
    void findAll_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/provinces")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    // ------------------- GET /api/provinces/{id} -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findById_ShouldReturnProvince() throws Exception {
        ProvinceResponse province = new ProvinceResponse(1, "Madrid");

        when(provinceService.findById(1)).thenReturn(province);

        mockMvc.perform(get("/api/provinces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Madrid"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findById_NotFound_ShouldReturn404() throws Exception {
        when(provinceService.findById(99)).thenThrow(new ResourceNotFoundException("Province not found"));

        mockMvc.perform(get("/api/provinces/99")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isNotFound());
    }

    // ------------------- GET /api/provinces/{id}/cities -------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findCitiesByProvince_ShouldReturnCities() throws Exception {
        when(cityService.findByProvince(eq(1), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(), 0, 0, 0, 0));

        mockMvc.perform(get("/api/provinces/1/cities")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    // ------------------- POST /api/provinces -------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void addProvince_ShouldReturnCreated() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                1,
                "Sevilla"
        );

        ProvinceResponse response = new ProvinceResponse(3, "Sevilla");

        when(provinceService.addProvince(any(ProvinceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/provinces/3"));
    }

    @Test
    void addProvince_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                1,
                "Sevilla"
        );

        mockMvc.perform(post("/api/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addProvince_WithWrongRole_ShouldReturnForbidden() throws Exception {
        ProvinceRequest json = new ProvinceRequest(
                1,
                "Sevilla"
        );
        mockMvc.perform(post("/api/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProvince_WithMissingCountryId_ShouldReturnBadRequest() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                null,
                "Sevilla"
        );

        mockMvc.perform(post("/api/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("countryId: must not be null"));
    }

    // ------------------- PUT /api/provinces/{id} -------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProvince_ShouldReturnUpdated() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                1,
                "Valencia"
        );

        ProvinceResponse response = new ProvinceResponse(1, "Valencia");

        when(provinceService.updateProvince(eq(1), any(ProvinceRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/provinces/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Valencia"));
    }

    @Test
    void updateProvince_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                1,
                "Valencia"
        );

        mockMvc.perform(put("/api/provinces/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void updateProvince_WithWrongRole_ShouldReturnForbidden() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                1,
                "Valencia"
        );

        mockMvc.perform(put("/api/provinces/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProvince_WithMissingCountryId_ShouldReturnBadRequest() throws Exception {
        ProvinceRequest request = new ProvinceRequest(
                null,
                "Valencia"
        );

        mockMvc.perform(put("/api/provinces/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("countryId: must not be null"));
    }

    // ------------------- DELETE /api/provinces/{id} -------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProvince_ShouldReturnNoContent() throws Exception {
        doNothing().when(provinceService).deleteProvince(1);

        mockMvc.perform(delete("/api/provinces/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProvince_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/provinces/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteProvince_WithWrongRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/provinces/1"))
                .andExpect(status().isForbidden());
    }
}
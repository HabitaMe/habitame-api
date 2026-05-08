package com.habitame.api.city.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.city.dto.CityRequest;
import com.habitame.api.city.dto.CityResponse;
import com.habitame.api.city.service.CityService;
import com.habitame.api.common.wrapper.PageResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    void findAll_WithoutAuth_ShouldReturn200() throws Exception {
        when(cityService.findAll(any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(new CityResponse(1, "Madrid", null)), 0, 10, 1, 1));

        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Madrid"));
    }

    @Test
    void findById_WithoutAuth_ShouldReturn200() throws Exception {
        when(cityService.findById(1)).thenReturn(new CityResponse(1, "Madrid", null));

        mockMvc.perform(get("/api/cities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void saveCity_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CityRequest("Sevilla", 1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void saveCity_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CityRequest("Sevilla", 1))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveCity_ShouldReturnCreated() throws Exception {
        when(cityService.saveCity(any())).thenReturn(new CityResponse(5, "Sevilla", null));

        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CityRequest("Sevilla", 1))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "api/cities/5"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveCity_WithMissingName_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CityRequest("", 1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCity_ShouldReturnNoContent() throws Exception {
        doNothing().when(cityService).deleteCity(1);

        mockMvc.perform(delete("/api/cities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteCity_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/cities/1"))
                .andExpect(status().isForbidden());
    }
}

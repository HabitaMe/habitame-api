package com.habitame.api.country.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.country.dto.CountryRequest;
import com.habitame.api.country.dto.CountryResponse;
import com.habitame.api.country.service.CountryService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CountryService countryService;

    @MockBean
    private ProvinceService provinceService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    void findAll_WithoutAuth_ShouldReturn200() throws Exception {
        when(countryService.findAll(any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(new CountryResponse(1, "España", "ES")), 0, 10, 1, 1));

        mockMvc.perform(get("/v1/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("España"));
    }

    @Test
    void findById_WithoutAuth_ShouldReturn200() throws Exception {
        when(countryService.findById(1)).thenReturn(new CountryResponse(1, "España", "ES"));

        mockMvc.perform(get("/v1/countries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isoCode").value("ES"));
    }

    @Test
    void addCountry_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CountryRequest("Francia", "FR"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addCountry_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CountryRequest("Francia", "FR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCountry_ShouldReturnCreated() throws Exception {
        when(countryService.addCountry(any())).thenReturn(new CountryResponse(3, "Francia", "FR"));

        mockMvc.perform(post("/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CountryRequest("Francia", "FR"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/countries/3"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCountry_WithMissingIsoCode_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CountryRequest("Francia", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCountry_ShouldReturnNoContent() throws Exception {
        doNothing().when(countryService).deleteCountry(1);

        mockMvc.perform(delete("/v1/countries/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteCountry_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/v1/countries/1"))
                .andExpect(status().isForbidden());
    }
}

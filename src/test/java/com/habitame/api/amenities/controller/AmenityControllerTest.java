package com.habitame.api.amenities.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.amenities.dto.AmenityRequest;
import com.habitame.api.amenities.dto.AmenityResponse;
import com.habitame.api.amenities.entity.AmenityScope;
import com.habitame.api.amenities.service.AmenityService;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class AmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AmenityService amenityService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    // ------------------- GET /v1/amenities (público) -------------------

    @Test
    void findAmenities_WithoutAuth_ShouldReturn200() throws Exception {
        when(amenityService.findAmenities()).thenReturn(List.of(
                new AmenityResponse(1, "WiFi", "Conexión inalámbrica", "PROPERTY"),
                new AmenityResponse(2, "Parking", "Plaza de aparcamiento", "PROPERTY")
        ));

        mockMvc.perform(get("/v1/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("WiFi"));
    }

    @Test
    void findAmenitiesByScope_WithoutAuth_ShouldReturn200() throws Exception {
        when(amenityService.findAmenitiesByScope(AmenityScope.ROOM)).thenReturn(List.of(
                new AmenityResponse(3, "Armario", "Armario empotrado", "ROOM")
        ));

        mockMvc.perform(get("/v1/amenities/scope/ROOM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scope").value("ROOM"));
    }

    // ------------------- POST /v1/amenities (solo ADMIN) -------------------

    @Test
    void saveAmenity_WithoutAuth_ShouldReturn401() throws Exception {
        AmenityRequest request = new AmenityRequest("WiFi", "Conexión", AmenityScope.PROPERTY);

        mockMvc.perform(post("/v1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void saveAmenity_WithWrongRole_ShouldReturn403() throws Exception {
        AmenityRequest request = new AmenityRequest("WiFi", "Conexión", AmenityScope.PROPERTY);

        mockMvc.perform(post("/v1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveAmenity_ShouldReturnCreated() throws Exception {
        AmenityRequest request = new AmenityRequest("WiFi", "Conexión inalámbrica", AmenityScope.PROPERTY);
        AmenityResponse response = new AmenityResponse(10, "WiFi", "Conexión inalámbrica", "PROPERTY");

        when(amenityService.saveAmenity(any())).thenReturn(response);

        mockMvc.perform(post("/v1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "api/amenities/10"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveAmenity_WithMissingName_ShouldReturn400() throws Exception {
        AmenityRequest request = new AmenityRequest("", "Descripción", AmenityScope.PROPERTY);

        mockMvc.perform(post("/v1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ------------------- DELETE /v1/amenities/{id} -------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAmenity_ShouldReturnNoContent() throws Exception {
        doNothing().when(amenityService).deleteAmenity(1);

        mockMvc.perform(delete("/v1/amenities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteAmenity_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/v1/amenities/1"))
                .andExpect(status().isForbidden());
    }
}

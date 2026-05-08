package com.habitame.api.property.controller;

import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.property.dto.PropertyPublicDetailResponse;
import com.habitame.api.property.dto.PropertyPublicResponse;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PublicPropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getPropertyList_ShouldReturnPageWithoutAuth() throws Exception {
        PageResponse<PropertyPublicResponse> page = new PageResponse<>(List.of(), 0, 10, 0, 0);
        when(propertyService.findPublicProperties(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/public/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void findById_ShouldReturnPropertyWithoutAuth() throws Exception {
        PropertyPublicDetailResponse detail = new PropertyPublicDetailResponse(
                1, "Piso en Madrid", "Descripción", null, "Calle Mayor 1",
                null, null, null, null, null
        );
        when(propertyService.findPublicPropertyById(1)).thenReturn(detail);

        mockMvc.perform(get("/api/public/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Piso en Madrid"));
    }

    @Test
    void findById_WhenNotFound_ShouldReturn404() throws Exception {
        when(propertyService.findPublicPropertyById(99))
                .thenThrow(new ResourceNotFoundException("Property not found: 99"));

        mockMvc.perform(get("/api/public/properties/99"))
                .andExpect(status().isNotFound());
    }
}

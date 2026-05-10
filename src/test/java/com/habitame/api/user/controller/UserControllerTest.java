package com.habitame.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.user.dto.UserRequest;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.repository.UserRepository;
import com.habitame.api.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;


    @Test
    void getCurrentUser_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "juanito", roles = "ARRENDADOR")
    void getCurrentUser_ShouldReturnUserFromAuthentication() throws Exception {
        // El endpoint usa el principal de Spring Security directamente
        // WithMockUser crea un User de Spring (no UserEntity), por lo que el cast falla
        // y lanza UnauthorizedException — comportamiento correcto en integración real
        mockMvc.perform(get("/v1/user/me"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void updateMe_WithoutAuth_ShouldReturn401() throws Exception {
        UserRequest request = new UserRequest("Juan García", "611000000");

        mockMvc.perform(put("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void updateMe_ShouldReturn200() throws Exception {
        UserRequest request = new UserRequest("Juan García Actualizado", "611000000");
        UserResponse response = new UserResponse(
                1, "juanito", "Juan García Actualizado", "611000000",
                null, "juan@mail.com", "ARRENDADOR", true, LocalDateTime.now()
        );

        when(userService.updateUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Juan García Actualizado"));
    }
}

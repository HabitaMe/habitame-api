package com.habitame.api.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomOwnerRequest;
import com.habitame.api.room.dto.RoomOwnerResponse;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.service.RoomReviewService;
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
class OwnerRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomImageService roomImageService;

    @MockBean
    private RoomReviewService roomReviewService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;


    @Test
    void findMyRooms_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/owner/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void findMyRooms_ShouldReturn200() throws Exception {
        when(roomService.findAllByOwner(any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));

        mockMvc.perform(get("/v1/owner/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }


    @Test
    void addOwnerRoom_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/owner/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addOwnerRoom_WithMissingTitle_ShouldReturn400() throws Exception {
        RoomOwnerRequest request = new RoomOwnerRequest(
                "", "Descripción", BigDecimal.valueOf(20), 1, BigDecimal.valueOf(400), 1, 1
        );

        mockMvc.perform(post("/v1/owner/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void addOwnerRoom_ShouldReturnCreated() throws Exception {
        RoomOwnerResponse response = new RoomOwnerResponse(
                5, "Habitación doble", BigDecimal.valueOf(400), BigDecimal.valueOf(20), null, "IN_REVIEW"
        );
        when(roomService.addOwnerRoom(any())).thenReturn(response);

        mockMvc.perform(post("/v1/owner/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "v1/owner/rooms/5"));
    }


    @Test
    void deleteOwnerRoom_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/v1/owner/rooms/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void deleteOwnerRoom_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/v1/owner/rooms/1"))
                .andExpect(status().isNoContent());
    }

    private RoomOwnerRequest buildValidRequest() {
        return new RoomOwnerRequest(
                "Habitación doble", "Descripción completa",
                BigDecimal.valueOf(20), 2, BigDecimal.valueOf(400), 1, 1
        );
    }
}

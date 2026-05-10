package com.habitame.api.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.wrapper.PageResponse;
import com.habitame.api.room.dto.RoomAdminRequest;
import com.habitame.api.room.dto.RoomAdminResponse;
import com.habitame.api.room.entity.RoomStatus;
import com.habitame.api.room.service.RoomService;
import com.habitame.api.roomImage.service.RoomImageService;
import com.habitame.api.roomReview.dto.RoomReviewDecisionRequest;
import com.habitame.api.roomReview.dto.RoomReviewResponse;
import com.habitame.api.roomReview.entity.RoomReviewStatus;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRoomControllerTest {

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

    // ------------------- GET /v1/admin/rooms -------------------

    @Test
    void findAll_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/admin/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void findAll_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/v1/admin/rooms"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_ShouldReturnPage() throws Exception {
        when(roomService.findAll(any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));

        mockMvc.perform(get("/v1/admin/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ------------------- POST /v1/admin/rooms -------------------

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void saveRoom_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/v1/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveRoom_WithMissingTitle_ShouldReturn400() throws Exception {
        RoomAdminRequest request = new RoomAdminRequest(
                "", "Desc", BigDecimal.valueOf(20), 1, BigDecimal.valueOf(400), 1, 1, RoomStatus.ACTIVE, 1
        );

        mockMvc.perform(post("/v1/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveRoom_ShouldReturnCreated() throws Exception {
        RoomAdminResponse response = new RoomAdminResponse(7, "Hab. doble", null, "ACTIVE", null);
        when(roomService.saveAdminRoom(any())).thenReturn(response);

        mockMvc.perform(post("/v1/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "v1/admin/rooms/7"));
    }

    // ------------------- DELETE /v1/admin/rooms/{id} -------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRoom_ShouldReturnNoContent() throws Exception {
        doNothing().when(roomService).deleteRoom(1);

        mockMvc.perform(delete("/v1/admin/rooms/1"))
                .andExpect(status().isNoContent());
    }

    // ------------------- PATCH /v1/admin/rooms/{id}/reviews/resolve -------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void resolveReview_ShouldReturn200() throws Exception {
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.APPROVED, null);
        RoomReviewResponse response = new RoomReviewResponse(1, "APPROVED", 1);

        when(roomService.resolveReview(eq(1), any())).thenReturn(response);

        mockMvc.perform(patch("/v1/admin/rooms/1/reviews/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void resolveReview_WithWrongRole_ShouldReturn403() throws Exception {
        RoomReviewDecisionRequest request = new RoomReviewDecisionRequest(RoomReviewStatus.APPROVED, null);

        mockMvc.perform(patch("/v1/admin/rooms/1/reviews/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private RoomAdminRequest buildValidRequest() {
        return new RoomAdminRequest(
                "Habitación doble", "Descripción completa", BigDecimal.valueOf(20),
                2, BigDecimal.valueOf(400), 1, 1, RoomStatus.ACTIVE, 1
        );
    }
}

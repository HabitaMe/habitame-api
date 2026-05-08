package com.habitame.api.roomReview.controller;

import com.habitame.api.auth.security.JwtProvider;
import com.habitame.api.common.wrapper.PageResponse;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RoomReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomReviewService roomReviewService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getReviews_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/room-reviews"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ARRENDADOR")
    void getReviews_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/room-reviews"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReviews_ShouldReturnPage() throws Exception {
        PageResponse<RoomReviewResponse> page = new PageResponse<>(
                List.of(new RoomReviewResponse(1, "PENDING", 3)),
                0, 10, 1, 1
        );
        when(roomReviewService.getReviews(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/room-reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReviewsByStatus_ShouldFilterCorrectly() throws Exception {
        PageResponse<RoomReviewResponse> page = new PageResponse<>(
                List.of(new RoomReviewResponse(2, "APPROVED", 4)),
                0, 10, 1, 1
        );
        when(roomReviewService.getReviewsByStatus(eq(RoomReviewStatus.APPROVED), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/room-reviews/status/APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("APPROVED"));
    }
}

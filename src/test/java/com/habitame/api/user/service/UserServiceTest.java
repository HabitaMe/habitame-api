package com.habitame.api.user.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.user.dto.UserRequest;
import com.habitame.api.user.dto.UserResponse;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private UserService userService;

    private MockedStatic<SecurityUtils> securityUtils;
    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        currentUser = buildUser(1, Role.ARRENDADOR);
        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1);
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(currentUser);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }


    @Test
    void addPhoto_ShouldStoreAndUpdateUrl() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "photo", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
        when(imageStorageService.store(file, "profiles")).thenReturn("/uploads/profiles/foto.jpg");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.addPhoto(1, file);

        assertThat(response.photoUrl()).isEqualTo("/uploads/profiles/foto.jpg");
        verify(imageStorageService).store(file, "profiles");
    }

    @Test
    void addPhoto_WhenDifferentUserAndNotAdmin_ShouldThrow() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "photo", "foto.jpg", "image/jpeg", new byte[]{1}
        );

        assertThatThrownBy(() -> userService.addPhoto(99, file))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void addPhoto_WhenAdminModifiesOtherUser_ShouldPass() throws IOException {
        securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(99);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(true);

        UserEntity targetUser = buildUser(1, Role.ARRENDADOR);
        MockMultipartFile file = new MockMultipartFile(
                "photo", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(targetUser));
        when(imageStorageService.store(any(), eq("profiles"))).thenReturn("/uploads/foto.jpg");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.addPhoto(1, file);

        assertThat(response.photoUrl()).isEqualTo("/uploads/foto.jpg");
    }

    @Test
    void addPhoto_WithNonImageFile_ShouldThrow() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[]{1}
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));

        assertThatThrownBy(() -> userService.addPhoto(1, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("image");
    }

    @Test
    void addPhoto_WithEmptyFile_ShouldThrow() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "photo", "foto.jpg", "image/jpeg", new byte[0]
        );

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));

        assertThatThrownBy(() -> userService.addPhoto(1, file))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void removePhoto_ShouldClearPhotoUrl() throws IOException {
        currentUser.setPhotoUrl("/uploads/foto.jpg");

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.removePhoto(1);

        assertThat(response.photoUrl()).isNull();
        verify(imageStorageService).delete("/uploads/foto.jpg");
    }

    @Test
    void removePhoto_WhenDifferentUserAndNotAdmin_ShouldThrow() {
        assertThatThrownBy(() -> userService.removePhoto(99))
                .isInstanceOf(UnauthorizedException.class);
    }


    @Test
    void updateUser_ShouldUpdateOnlyFullNameAndPhone() {
        UserRequest request = new UserRequest("Juan García Actualizado", "611000000");

        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.updateUser(request);

        assertThat(response.fullName()).isEqualTo("Juan García Actualizado");
        assertThat(response.phone()).isEqualTo("611000000");
        assertThat(response.username()).isEqualTo(currentUser.getUsername());
    }


    private UserEntity buildUser(Integer id, Role role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("juanito");
        user.setEmail("juan@mail.com");
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}

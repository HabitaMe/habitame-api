package com.habitame.api.room.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.common.exception.ResourceNotFoundException;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.room.entity.RoomEntity;
import com.habitame.api.room.entity.RoomStatus;
import com.habitame.api.room.repository.RoomRepository;
import com.habitame.api.roomImage.entity.RoomImageEntity;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomSecurityServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomSecurityService roomSecurityService;

    private MockedStatic<SecurityUtils> securityUtils;

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }


    @Test
    void checkRoomAccess_WhenAdmin_ShouldPass() {
        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(true);

        UserEntity owner = buildUser(1, Role.ARRENDADOR);
        RoomEntity room = buildRoom(owner);

        assertThatCode(() -> roomSecurityService.checkRoomAccess(room))
                .doesNotThrowAnyException();
    }

    @Test
    void checkRoomAccess_WhenOwner_ShouldPass() {
        UserEntity owner = buildUser(1, Role.ARRENDADOR);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
        securityUtils.when(() -> SecurityUtils.isOwnerOf(owner)).thenReturn(true);

        RoomEntity room = buildRoom(owner);

        assertThatCode(() -> roomSecurityService.checkRoomAccess(room))
                .doesNotThrowAnyException();
    }

    @Test
    void checkRoomAccess_WhenOtherUser_ShouldThrow() {
        UserEntity owner = buildUser(1, Role.ARRENDADOR);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
        securityUtils.when(() -> SecurityUtils.isOwnerOf(owner)).thenReturn(false);

        RoomEntity room = buildRoom(owner);

        assertThatThrownBy(() -> roomSecurityService.checkRoomAccess(room))
                .isInstanceOf(ForbiddenException.class);
    }


    @Test
    void checkRoomAccess_ById_WhenNotFound_ShouldThrow() {
        securityUtils = mockStatic(SecurityUtils.class);

        when(roomRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomSecurityService.checkRoomAccess(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found: 99");
    }


    @Test
    void checkImageAccess_WhenAdmin_ShouldPass() {
        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(true);

        UserEntity owner = buildUser(1, Role.ARRENDADOR);
        RoomImageEntity image = buildImage(buildRoom(owner));

        assertThatCode(() -> roomSecurityService.checkImageAccess(image))
                .doesNotThrowAnyException();
    }

    @Test
    void checkImageAccess_WhenNotOwnerAndNotAdmin_ShouldThrow() {
        UserEntity owner = buildUser(1, Role.ARRENDADOR);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
        securityUtils.when(() -> SecurityUtils.isOwnerOf(owner)).thenReturn(false);

        RoomImageEntity image = buildImage(buildRoom(owner));

        assertThatThrownBy(() -> roomSecurityService.checkImageAccess(image))
                .isInstanceOf(ForbiddenException.class);
    }


    private UserEntity buildUser(Integer id, Role role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private RoomEntity buildRoom(UserEntity owner) {
        PropertyEntity property = PropertyEntity.builder()
                .id(10)
                .owner(owner)
                .images(new ArrayList<>())
                .propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();

        return RoomEntity.builder()
                .id(1)
                .title("Habitación")
                .status(RoomStatus.ACTIVE)
                .property(property)
                .roomAmenities(new ArrayList<>())
                .images(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();
    }

    private RoomImageEntity buildImage(RoomEntity room) {
        return RoomImageEntity.builder()
                .id(1)
                .room(room)
                .imageUrl("https://example.com/img.jpg")
                .build();
    }
}

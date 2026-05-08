package com.habitame.api.property.service;

import com.habitame.api.auth.security.SecurityUtils;
import com.habitame.api.common.exception.ForbiddenException;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.repository.PropertyRepository;
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class PropertySecurityServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertySecurityService propertySecurityService;

    private MockedStatic<SecurityUtils> securityUtils;

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }

    @Test
    void checkPropertyAccess_WhenAdmin_ShouldPass() {
        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(true);

        PropertyEntity property = buildProperty(ownedBy(99));

        assertThatCode(() -> propertySecurityService.checkPropertyAccess(property))
                .doesNotThrowAnyException();
    }

    @Test
    void checkPropertyAccess_WhenOwner_ShouldPass() {
        UserEntity owner = buildUser(1, Role.ARRENDADOR);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
        securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1);
        securityUtils.when(() -> SecurityUtils.isOwnerOf(owner)).thenReturn(true);

        PropertyEntity property = buildProperty(owner);

        assertThatCode(() -> propertySecurityService.checkPropertyAccess(property))
                .doesNotThrowAnyException();
    }

    @Test
    void checkPropertyAccess_WhenOtherUser_ShouldThrow() {
        UserEntity owner = buildUser(1, Role.ARRENDADOR);

        securityUtils = mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
        securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(2);
        securityUtils.when(() -> SecurityUtils.isOwnerOf(owner)).thenReturn(false);

        PropertyEntity property = buildProperty(owner);

        assertThatThrownBy(() -> propertySecurityService.checkPropertyAccess(property))
                .isInstanceOf(ForbiddenException.class);
    }

    // ------------------- helpers -------------------

    private UserEntity buildUser(Integer id, Role role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private UserEntity ownedBy(Integer id) {
        return buildUser(id, Role.ARRENDADOR);
    }

    private PropertyEntity buildProperty(UserEntity owner) {
        return PropertyEntity.builder()
                .id(1)
                .title("Piso")
                .description("Descripción")
                .address("Calle")
                .owner(owner)
                .images(new ArrayList<>())
                .propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();
    }
}

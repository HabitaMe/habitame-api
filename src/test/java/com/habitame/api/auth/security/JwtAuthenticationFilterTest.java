package com.habitame.api.auth.security;

import com.habitame.api.common.exception.UnauthorizedException;
import com.habitame.api.user.entity.Role;
import com.habitame.api.user.entity.UserEntity;
import com.habitame.api.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithNoAuthHeader_ShouldNotSetAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNonBearerHeader_ShouldNotSetAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws Exception {
        UserEntity user = buildUser("juanito", Role.ARRENDADOR);

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtProvider.getSubjectFromToken("valid-token")).thenReturn("juanito");
        when(userRepository.findByUsername("juanito")).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ARRENDADOR"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidTokenButUnknownUser_ShouldNotSetAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtProvider.getSubjectFromToken("valid-token")).thenReturn("fantasma");
        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldPropagateException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(jwtProvider.validateToken("expired-token")).thenThrow(new UnauthorizedException("Expired token"));

        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> filter.doFilterInternal(request, response, filterChain)
        ).isInstanceOf(UnauthorizedException.class);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private UserEntity buildUser(String username, Role role) {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setUsername(username);
        user.setRole(role);
        user.setIsActive(true);
        return user;
    }
}

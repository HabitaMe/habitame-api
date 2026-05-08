package com.habitame.api.auth.security;

import com.habitame.api.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        // Secret de al menos 32 chars para HMAC-SHA256
        jwtProvider = new JwtProvider(
                "clave-super-secreta-para-tests-unitarios-12345",
                3600000L,
                86400000L
        );
    }

    @Test
    void generateAccessToken_ShouldReturnValidJwt() {
        String token = jwtProvider.generateAccessToken("juanito");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void getSubjectFromToken_ShouldExtractUsername() {
        String token = jwtProvider.generateAccessToken("juanito");

        assertThat(jwtProvider.getSubjectFromToken(token)).isEqualTo("juanito");
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtProvider.generateAccessToken("juanito");

        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_WithTamperedToken_ShouldThrow() {
        String token = jwtProvider.generateAccessToken("juanito");
        String tampered = token.substring(0, token.length() - 5) + "xxxxx";

        assertThatThrownBy(() -> jwtProvider.validateToken(tampered))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    void validateToken_WithExpiredToken_ShouldThrow() {
        JwtProvider shortLived = new JwtProvider(
                "clave-super-secreta-para-tests-unitarios-12345",
                -1000L,  // ya expirado
                86400000L
        );
        String token = shortLived.generateAccessToken("juanito");

        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Expired token");
    }

    @Test
    void getSubjectFromToken_WithGarbageToken_ShouldThrow() {
        assertThatThrownBy(() -> jwtProvider.getSubjectFromToken("esto.no.es.un.jwt"))
                .isInstanceOf(UnauthorizedException.class);
    }
}

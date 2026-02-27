package com.habitame.api.auth.security;

import com.habitame.api.common.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;

    public JwtProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration:3600000}") long accessExpirationMillis,
            @Value("${jwt.refresh-expiration:86400000}") long refreshExpirationMillis
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    /**
     * Genera un token de acceso de corta duración (default 1h).
     */
    public String generateAccessToken(String subject) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un refresh token de larga duración (default 24h).
     * Configurar jwt.refresh-expiration en application.yml para cambiarlo.
     */
    public String generateRefreshToken(String subject) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida la firma y expiración del token.
     *
     * @throws UnauthorizedException si el token expiró o es inválido
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException("Expired token");
        } catch (JwtException ex) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    /**
     * Extrae el subject del token.
     *
     * @throws UnauthorizedException si el token no puede ser parseado
     */
    public String getSubjectFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException ex) {
            throw new UnauthorizedException("The token could not be read");
        }
    }
}
package com.habitame.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        // Endpoints de administración: solo accesibles desde el panel admin
        CorsConfiguration adminConfig = new CorsConfiguration();
        adminConfig.setAllowedOrigins(List.of("https://admin.habitame.xyz"));
        adminConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        adminConfig.setAllowedHeaders(List.of("*"));
        adminConfig.setAllowCredentials(false);

        // Resto de endpoints: abierto para la app móvil (IPs dinámicas)
        CorsConfiguration generalConfig = new CorsConfiguration();
        generalConfig.setAllowedOrigins(List.of("*"));
        generalConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        generalConfig.setAllowedHeaders(List.of("*"));
        generalConfig.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/v1/admin/**", adminConfig);
        source.registerCorsConfiguration("/**", generalConfig);
        return source;
    }
}
package com.habitame.api.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "HabitaMe API",
                version = "v1",
                description = "API de la plataforma HabitaMe para el alquiler de habitaciones y propiedades. " +
                        "Los roles disponibles son ARRENDADOR (propietario), ARRENDATARIO (inquilino) y ADMIN."
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtenido al hacer login en /v1/auth/login"
)
public class OpenApiConfig {}

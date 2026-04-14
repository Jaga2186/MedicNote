package com.MedicNote.patientService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MedicNote Patient Service API",
                version = "1.0",
                description = "REST API for managing patients in the MedicNote system",
                contact = @Contact(name = "MedicNote Team")
        ),
        servers = @Server(url = "/", description = "Default Server"),
        security = @SecurityRequirement(name = "bearer-jwt")
)
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter your JWT token (obtained from /api/auth/doctor/login or /api/auth/patient/login)"
)
public class OpenApiConfig {
}

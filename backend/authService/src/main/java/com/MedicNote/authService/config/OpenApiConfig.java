package com.MedicNote.authService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MedicNote Auth Service API",
                version = "1.0",
                description = "Centralized authentication service for the MedicNote system",
                contact = @Contact(name = "MedicNote Team")
        ),
        servers = @Server(url = "/", description = "Default Server")
)
public class OpenApiConfig {
}

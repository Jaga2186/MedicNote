package com.MedicNote.prescriptionService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MedicNote Prescription Service API",
                version = "1.0",
                description = "REST API for managing prescriptions, PDF generation, and email in the MedicNote system",
                contact = @Contact(name = "MedicNote Team")
        ),
        servers = @Server(url = "/", description = "Default Server")
)
public class OpenApiConfig {
}

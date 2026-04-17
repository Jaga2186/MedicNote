package com.MedicNote.patientService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequestDTO {

    @Schema(description = "Email address", example = "jane.doe@email.com or 9876543210")
    @NotBlank(message = "Email or phone is required")
    private String identifier;

    @Schema(description = "Password", example = "Pass@123")
    @NotBlank(message = "Password is required")
    private String password;
}
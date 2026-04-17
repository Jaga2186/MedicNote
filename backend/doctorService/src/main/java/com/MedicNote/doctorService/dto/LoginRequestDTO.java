package com.MedicNote.doctorService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request")
@Data
public class LoginRequestDTO {
    @Schema(description = "Email address", example = "john.smith@hospital.com or 9876543210")
    @NotBlank(message = "Email or phone is required")
    private String identifier;

    @Schema(description = "Password", example = "Pass@123")
    @NotBlank(message = "Password is required")
    private String password;
}

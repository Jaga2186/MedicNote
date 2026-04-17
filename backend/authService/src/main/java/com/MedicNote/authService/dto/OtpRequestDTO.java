package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to send OTP")
public class OtpRequestDTO {

    @Schema(description = "Email or phone number", example = "john.smith@hospital.com or 9876543210")
    @NotBlank(message = "Email or phone is required")
    private String identifier;

    @Schema(description = "Role: DOCTOR or PATIENT", example = "DOCTOR")
    @NotBlank(message = "Role is required")
    private String role;
}

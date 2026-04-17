package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to verify OTP and login")
public class OtpVerifyRequestDTO {

    @Schema(description = "Email or phone number used when requesting OTP", example = "john.smith@hospital.com or 9876543210")
    @NotBlank(message = "Email or phone is required")
    private String identifier;

    @Schema(description = "Role: DOCTOR or PATIENT", example = "DOCTOR")
    @NotBlank(message = "Role is required")
    private String role;

    @Schema(description = "6-digit OTP code", example = "483921")
    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otpCode;
}

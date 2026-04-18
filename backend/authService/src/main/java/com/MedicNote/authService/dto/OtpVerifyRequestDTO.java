package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Step 2 — Verify OTP and get JWT")
public class OtpVerifyRequestDTO {

    @Schema(description = "Session token received from login step", example = "a1b2c3d4-...")
    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @Schema(description = "6-digit OTP code", example = "483921")
    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otpCode;
}

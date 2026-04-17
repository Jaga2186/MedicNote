package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequestDTO {

    @Schema(description = "User email address", example = "doctor@medicnote.com or 9876543210")
    @NotBlank(message = "Email or phone is required")
    private String identifier;

    @Schema(description = "User password", example = "P@ssw0rd123")
    @NotBlank(message = "Password is required")
    private String password;
}

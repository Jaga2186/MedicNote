package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequestDTO {

    @Schema(description = "User email address", example = "doctor@medicnote.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "P@ssw0rd123")
    @NotBlank(message = "Password is required")
    private String password;
}

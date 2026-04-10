package com.MedicNote.doctorService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request")
@Data
public class LoginRequestDTO {
    @Schema(description = "Email address", example = "john.smith@hospital.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password", example = "Pass@123")
    @NotBlank(message = "Password is required")
    private String password;
}

package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Authentication response")
public class AuthResponseDTO {

    @Schema(description = "Response message", example = "Login successful")
    private String message;

    @Schema(description = "JWT authentication token")
    private String token;

    @Schema(description = "User role", example = "DOCTOR")
    private String role;

    @Schema(description = "Additional user data")
    private Object data;
}

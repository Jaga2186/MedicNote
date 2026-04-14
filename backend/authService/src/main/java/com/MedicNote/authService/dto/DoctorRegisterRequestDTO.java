package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Doctor registration request")
public class DoctorRegisterRequestDTO {

    @Schema(description = "Full name of the doctor", example = "Dr. John Smith")
    @NotBlank(message = "Doctor name is required")
    @Size(max = 100, message = "Doctor name must be less than 100 characters")
    private String doctorName;

    @Schema(description = "Email address", example = "john.smith@hospital.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String doctorEmail;

    @Schema(description = "Password (min 6 chars, must include letter, number, and special character)", example = "Pass@123")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$",
            message = "Password must be at least 6 characters and include letter, number, and special character"
    )
    private String doctorPassword;

    @Schema(description = "Phone number (10 digits)", example = "9876543210")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String doctorPhone;

    @Schema(description = "Medical specialization", example = "Cardiology")
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Schema(description = "Medical license number", example = "MCI-12345")
    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Invalid license number")
    private String licenseNumber;

    @Schema(description = "Years of experience", example = "10")
    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience must be >= 0")
    @Max(value = 60, message = "Experience must be <= 60")
    private Integer experienceYears;

    @Schema(description = "Hospital name", example = "Apollo Hospital")
    @NotBlank(message = "Hospital name is required")
    private String hospitalName;
}

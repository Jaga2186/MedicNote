package com.MedicNote.patientService.dto;

import lombok.Data;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import com.MedicNote.patientService.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Patient registration request")
public class PatientRequestDTO {

    @Schema(description = "Full name", example = "Jane Doe")
    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name must be less than 100 characters")
    private String patientName;

    @Schema(description = "Email address", example = "jane.doe@email.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String patientEmail;

    @Schema(description = "Password", example = "Pass@123")
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$",
            message = "Password must be at least 6 characters and include letter, number, and special character"
    )
    private String patientPassword;

    @Schema(description = "Phone number", example = "9876543210")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String patientPhone;

    @Schema(description = "Date of birth", example = "1990-05-15")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender")
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Valid
    @NotNull(message = "Address is required")
    private AddressDTO address;

    @Valid
    @NotNull(message = "Emergency contact is required")
    private EmergencyContactDTO emergencyContact;

    @Valid
    @NotNull(message = "Medical info is required")
    private MedicalInfoDTO medicalInfo;
}
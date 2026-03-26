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

@Data
public class PatientRequestDTO {

    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name must be less than 100 characters")
    private String patientName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String patientEmail;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$",
            message = "Password must be at least 6 characters and include letter, number, and special character"
    )
    private String patientPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String patientPhone;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

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
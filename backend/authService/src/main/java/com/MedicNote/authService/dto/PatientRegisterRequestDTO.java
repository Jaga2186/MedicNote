package com.MedicNote.authService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;

@Data
@Schema(description = "Patient registration request")
public class PatientRegisterRequestDTO {

    @Schema(description = "Full name", example = "Jane Doe")
    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name must be less than 100 characters")
    private String patientName;

    @Schema(description = "Email address", example = "jane.doe@email.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String patientEmail;

    @Schema(description = "Password (min 6 chars, must include letter, number, and special character)", example = "Pass@123")
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$",
            message = "Password must be at least 6 characters and include letter, number, and special character"
    )
    private String patientPassword;

    @Schema(description = "Phone number (10 digits)", example = "9876543210")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String patientPhone;

    @Schema(description = "Date of birth", example = "1990-05-15")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender", example = "MALE")
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

    // ---- Nested types ----

    public enum Gender {
        MALE, FEMALE, OTHER;

        @JsonCreator
        public static Gender from(String value) {
            if (value == null) return null;
            return Gender.valueOf(value.toUpperCase());
        }
    }

    public enum BloodGroup {
        A_POS, A_NEG, B_POS, B_NEG, O_POS, O_NEG, AB_POS, AB_NEG;

        @JsonCreator
        public static BloodGroup from(String value) {
            if (value == null) return null;
            return BloodGroup.valueOf(value.toUpperCase());
        }
    }

    @Data
    @Schema(description = "Address details")
    public static class AddressDTO {
        @Schema(description = "Street address", example = "123 Main St")
        @NotBlank(message = "Street is required")
        @Size(max = 150, message = "Street must be less than 150 characters")
        private String street;

        @Schema(description = "City", example = "Chennai")
        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must be less than 100 characters")
        private String city;

        @Schema(description = "State", example = "Tamil Nadu")
        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must be less than 100 characters")
        private String state;

        @Schema(description = "Country", example = "India")
        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must be less than 100 characters")
        private String country;

        @Schema(description = "Pin code (6 digits)", example = "600001")
        @NotBlank(message = "PinCode is required")
        @Pattern(regexp = "\\d{6}", message = "PinCode must be 6 digits")
        private String pinCode;
    }

    @Data
    @Schema(description = "Emergency contact details")
    public static class EmergencyContactDTO {
        @Schema(description = "Contact name", example = "John Doe")
        @NotBlank(message = "Contact name is required")
        @Size(max = 100, message = "Contact name must be less than 100 characters")
        private String name;

        @Schema(description = "Contact phone number", example = "9876543211")
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
        private String phone;

        @Schema(description = "Relation to patient", example = "Father")
        @NotBlank(message = "Relation is required")
        @Size(max = 50, message = "Relation must be less than 50 characters")
        private String relation;
    }

    @Data
    @Schema(description = "Medical information")
    public static class MedicalInfoDTO {
        @Schema(description = "Blood group", example = "O_POS")
        @NotNull(message = "Blood group is required")
        private BloodGroup bloodGroup;

        @Schema(description = "Known allergies", example = "Penicillin")
        @Size(max = 500, message = "Allergies must be less than 500 characters")
        private String allergies;

        @Schema(description = "Medical history", example = "Asthma")
        @Size(max = 500, message = "Medical history must be less than 500 characters")
        private String medicalHistory;

        @Schema(description = "Current medications", example = "Inhaler")
        @Size(max = 500, message = "Current medications must be less than 500 characters")
        private String currentMedications;
    }
}

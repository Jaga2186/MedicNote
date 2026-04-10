package com.MedicNote.patientService.dto;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Data
public class EmergencyContactDTO {

    @NotBlank(message = "Contact name is required")
    @Size(max = 100, message = "Contact name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String phone;

    @NotBlank(message = "Relation is required")
    @Size(max = 50, message = "Relation must be less than 50 characters")
    private String relation; // ✅ matches entity
}
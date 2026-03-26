package com.MedicNote.patientService.dto;

import lombok.Data;

import jakarta.validation.constraints.*;

import com.MedicNote.patientService.entity.BloodGroup;

@Data
public class MedicalInfoDTO {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @Size(max = 500, message = "Allergies must be less than 500 characters")
    private String allergies;

    @Size(max = 500, message = "Medical history must be less than 500 characters")
    private String medicalHistory;

    @Size(max = 500, message = "Current medications must be less than 500 characters")
    private String currentMedications;
}
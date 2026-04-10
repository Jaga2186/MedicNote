package com.MedicNote.prescriptionService.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "Medication details")
public class MedicationDTO {

    @NotBlank(message = "Medicine name is required")
    @Size(max = 150, message = "Medicine name must be less than 150 characters")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage must be less than 100 characters")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Size(max = 100, message = "Frequency must be less than 100 characters")
    private String frequency;

    @NotBlank(message = "Duration is required")
    @Size(max = 100, message = "Duration must be less than 100 characters")
    private String duration;

    @Size(max = 500, message = "Instructions must be less than 500 characters")
    private String instructions;
}

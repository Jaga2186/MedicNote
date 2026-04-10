package com.MedicNote.prescriptionService.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
@Schema(description = "Prescription creation request")
public class PrescriptionRequestDTO {

    @Schema(description = "ID of the prescribing doctor", example = "1")
    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @Schema(description = "ID of the patient", example = "1")
    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be positive")
    private Long patientId;

    @Schema(description = "Diagnosis for the prescription", example = "Common cold with mild fever")
    @NotBlank(message = "Diagnosis is required")
    @Size(max = 500, message = "Diagnosis must be less than 500 characters")
    private String diagnosis;

    @Schema(description = "Additional notes for the prescription", example = "Follow up after 1 week")
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;

    @Schema(description = "List of medications prescribed")
    @Valid
    @NotEmpty(message = "At least one medication is required")
    private List<MedicationDTO> medications;
}

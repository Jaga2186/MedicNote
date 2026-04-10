package com.MedicNote.prescriptionService.dto;

import com.MedicNote.prescriptionService.entity.PrescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Prescription response")
public class PrescriptionResponseDTO {

    private Long prescriptionId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String diagnosis;
    private String notes;
    private PrescriptionStatus status;
    private List<MedicationDTO> medications;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.MedicNote.patientService.dto;

import lombok.Data;

@Data
public class MedicalInfoDTO {
    private String bloodGroup;
    private String allergies;
    private String medicalHistory;
    private String currentMedications;
}
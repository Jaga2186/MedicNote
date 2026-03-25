package com.MedicNote.patientService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Embeddable
@Data
public class MedicalInfo {

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String allergies;
    private String medicalHistory;
    private String currentMedications;
}
package com.MedicNote.patientService.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class EmergencyContact {
    private String name;
    private String phone;
    private String relation;
}
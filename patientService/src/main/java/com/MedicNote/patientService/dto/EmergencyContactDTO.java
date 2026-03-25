package com.MedicNote.patientService.dto;

import lombok.Data;

@Data
public class EmergencyContactDTO {
    private String name;
    private String phone;
    private String relation;
}
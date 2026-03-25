package com.MedicNote.patientService.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientRequestDTO {

    private String patientName;
    private String patientEmail;
    private String patientPassword;
    private String patientPhone;
    private LocalDate dateOfBirth;
    private String gender;

    private AddressDTO address;
    private EmergencyContactDTO emergencyContact;
    private MedicalInfoDTO medicalInfo;
}
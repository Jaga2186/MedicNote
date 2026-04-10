package com.MedicNote.patientService.dto;

import com.MedicNote.patientService.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Patient response")
public class PatientResponseDTO {

    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private LocalDate dateOfBirth;
    private Gender gender;

    private AddressDTO address;
    private EmergencyContactDTO emergencyContact;
    private MedicalInfoDTO medicalInfo;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
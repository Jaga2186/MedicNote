package com.MedicNote.doctorService.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DoctorResponseDTO {
    
    private Long doctorId;
    private String doctorName;
    private String doctorEmail;
    private String doctorPhone;
    private String doctorSpecialization;

    private String licenseNumber;
    private Integer experienceYears;
    private String hospitalName;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

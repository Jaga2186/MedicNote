package com.MedicNote.patientService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "patients",
        indexes = {
                @Index(name = "idx_patient_email", columnList = "patient_email"),
                @Index(name = "idx_patient_phone", columnList = "patient_phone")
        }
)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;

    @Column(name = "patient_email", nullable = false, unique = true, length = 100)
    private String patientEmail;

    @JsonIgnore
    @Column(name = "patient_password", nullable = false)
    private String patientPassword;

    @Column(name = "patient_phone", nullable = false, length = 10)
    private String patientPhone;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Address address;

    @Embedded
    private EmergencyContact emergencyContact;

    @Embedded
    private MedicalInfo medicalInfo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
package com.MedicNote.doctorService.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "doctors",
        indexes = {
                @Index(name = "idx_doctor_email", columnList = "doctor_email"),
                @Index(name = "idx_license_number", columnList = "license_number")
        }
)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "doctor_name", nullable = false, length = 100)
    private String doctorName;

    @Column(name = "doctor_email", nullable = false, unique = true, length = 100)
    private String doctorEmail;

    @JsonIgnore
    @Column(name = "doctor_password", nullable = false, length = 255)
    private String doctorPassword;

    @Column(name = "doctor_phone", nullable = false, length = 10)
    private String doctorPhone;

    @Column(name = "doctor_specialization", nullable = false, length = 100)
    private String specialization;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "doctor_experience", nullable = false)
    private Integer experienceYears;

    @Column(name = "hospital_name", nullable = false, length = 100)
    private String hospitalName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

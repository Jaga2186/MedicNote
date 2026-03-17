package com.MedicNote.doctorService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
@Table(name = "doctors")

public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "doctor_name", nullable = false, length = 100)
    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @Column(name = "doctor_email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String doctorEmail;

    @JsonIgnore
    @Column(name = "doctor_password", nullable = false)
    @NotBlank(message = "Password is required")
    private String doctorPassword;

    @Column(name = "doctor_phone", nullable = false, length = 10)
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String doctorPhone;

    @Column(name = "doctor_specialization", nullable = false, length = 100)
    @NotBlank(message = "Specialization is required")
    private String doctorSpecialization;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @Column(name = "doctor_experience", nullable = false)
    @Min(value = 0, message = "Experience must be a positive number")
    @Max(value = 60, message = "Experience can not exceed 60 years")
    private Integer experienceYears;

    @Column(name = "hospital_name", nullable = false, length = 100)
    @NotBlank(message = "Hospital name is required")
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

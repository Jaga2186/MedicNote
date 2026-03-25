package com.MedicNote.patientService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.MedicNote.patientService.entity.PatientEntity;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByPatientEmail(String email);
}
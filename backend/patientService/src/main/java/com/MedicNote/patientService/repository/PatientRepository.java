package com.MedicNote.patientService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.MedicNote.patientService.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientEmail(String patientEmail);
    boolean existsByPatientEmail(String patientEmail);
    boolean existsByPatientPhone(String patientPhone);
    List<Patient> findByIsActiveTrue();
    Page<Patient> findByIsActiveTrue(Pageable pageable);
}
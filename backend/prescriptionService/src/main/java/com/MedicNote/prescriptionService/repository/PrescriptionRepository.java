package com.MedicNote.prescriptionService.repository;

import com.MedicNote.prescriptionService.entity.Prescription;
import com.MedicNote.prescriptionService.entity.PrescriptionStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByDoctorIdAndIsActiveTrue(Long doctorId);

    List<Prescription> findByPatientIdAndIsActiveTrue(Long patientId);

    List<Prescription> findByStatusAndIsActiveTrue(PrescriptionStatus status);

    List<Prescription> findByIsActiveTrue();

    Optional<Prescription> findByPrescriptionIdAndIsActiveTrue(Long prescriptionId);
}

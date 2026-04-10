package com.MedicNote.prescriptionService.service;

import com.MedicNote.prescriptionService.dto.PrescriptionRequestDTO;
import com.MedicNote.prescriptionService.dto.PrescriptionResponseDTO;
import com.MedicNote.prescriptionService.entity.PrescriptionStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrescriptionService {

    PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO request);

    PrescriptionResponseDTO getPrescriptionById(Long prescriptionId);

    List<PrescriptionResponseDTO> getAllPrescriptions();

    Page<PrescriptionResponseDTO> getAllPrescriptions(Pageable pageable);

    List<PrescriptionResponseDTO> getPrescriptionsByDoctorId(Long doctorId);

    List<PrescriptionResponseDTO> getPrescriptionsByPatientId(Long patientId);

    List<PrescriptionResponseDTO> getPrescriptionsByStatus(PrescriptionStatus status);

    PrescriptionResponseDTO updatePrescription(Long prescriptionId, PrescriptionRequestDTO request);

    PrescriptionResponseDTO updatePrescriptionStatus(Long prescriptionId, PrescriptionStatus status);

    void deletePrescription(Long prescriptionId);
}

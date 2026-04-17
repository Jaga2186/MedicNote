package com.MedicNote.patientService.service;

import java.util.List;
import com.MedicNote.patientService.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    PatientResponseDTO registerPatient(PatientRequestDTO request);
    PatientResponseDTO loginPatient(String identifier, String password);
    PatientResponseDTO getPatientById(Long patientId);
    List<PatientResponseDTO> getAllPatients();
    Page<PatientResponseDTO> getAllPatients(Pageable pageable);
    PatientResponseDTO updatePatient(Long patientId, PatientRequestDTO request);
    void deletePatient(Long patientId);
    PatientResponseDTO getPatientByEmail(String email);
    PatientResponseDTO getPatientByPhone(String phone);
}
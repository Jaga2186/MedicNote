package com.MedicNote.patientService.service;

import java.util.List;
import com.MedicNote.patientService.dto.*;

public interface PatientService {

    PatientResponseDTO registerPatient(PatientRequestDTO request);
    PatientResponseDTO loginPatient(String email, String password);
    PatientResponseDTO getPatientById(Long patientId);
    List<PatientResponseDTO> getAllPatients();
    PatientResponseDTO updatePatient(Long patientId, PatientRequestDTO request);
    void deletePatient(Long patientId);
}
package com.MedicNote.patientService.service;

import java.util.List;
import com.MedicNote.patientService.dto.*;

public interface PatientService {

    PatientResponseDTO registerPatient(PatientRequestDTO request);
    PatientResponseDTO getPatientById(Long id);
    List<PatientResponseDTO> getAllPatients();
    PatientResponseDTO updatePatient(Long id, PatientRequestDTO request);
    void deletePatient(Long id);
}
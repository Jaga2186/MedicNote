package com.MedicNote.prescriptionService.service;

import com.MedicNote.prescriptionService.dto.MedicationDTO;
import com.MedicNote.prescriptionService.dto.PrescriptionRequestDTO;
import com.MedicNote.prescriptionService.dto.PrescriptionResponseDTO;
import com.MedicNote.prescriptionService.entity.Medication;
import com.MedicNote.prescriptionService.entity.Prescription;
import com.MedicNote.prescriptionService.entity.PrescriptionStatus;
import com.MedicNote.prescriptionService.exception.BadRequestException;
import com.MedicNote.prescriptionService.exception.PrescriptionNotFoundException;
import com.MedicNote.prescriptionService.exception.ServiceUnavailableException;
import com.MedicNote.prescriptionService.feign.DoctorServiceClient;
import com.MedicNote.prescriptionService.feign.PatientServiceClient;
import com.MedicNote.prescriptionService.mapper.DTOMapper;
import com.MedicNote.prescriptionService.repository.PrescriptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImplementation implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DTOMapper mapper;
    private final DoctorServiceClient doctorServiceClient;
    private final PatientServiceClient patientServiceClient;

    // =========================================================
    // CREATE PRESCRIPTION
    // =========================================================
    @Override
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto) {

        if (dto == null)
            throw new BadRequestException("Request body cannot be null");

        validateId(dto.getDoctorId(), "Doctor ID");
        validateId(dto.getPatientId(), "Patient ID");

        log.info("Creating prescription for doctor={} patient={}", dto.getDoctorId(), dto.getPatientId());

        // Validate doctor exists via Feign
        String doctorName = fetchDoctorName(dto.getDoctorId());

        // Validate patient exists via Feign
        String patientName = fetchPatientName(dto.getPatientId());

        Prescription prescription = mapper.requestDTOtoEntity(dto);
        prescription.setDoctorName(doctorName);
        prescription.setPatientName(patientName);
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setIsActive(true);

        if(prescription.getMedications() == null) {
            prescription.setMedications(new ArrayList<>());
        }

        // Map and add medications
        if (dto.getMedications() != null) {
            for (MedicationDTO medDTO : dto.getMedications()) {
                Medication medication = mapper.medicationDTOtoEntity(medDTO);
                prescription.addMedication(medication);
            }
        }

        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Prescription created successfully. ID={}", saved.getPrescriptionId());

        return mapper.entityToResponseDTO(saved);
    }

    // =========================================================
    // GET PRESCRIPTION BY ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponseDTO getPrescriptionById(Long prescriptionId) {

        validateId(prescriptionId, "Prescription ID");

        Prescription prescription = prescriptionRepository.findByPrescriptionIdAndIsActiveTrue(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        return mapper.entityToResponseDTO(prescription);
    }

    // =========================================================
    // GET ALL PRESCRIPTIONS
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> getAllPrescriptions() {

        log.info("Fetching all active prescriptions");

        return prescriptionRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionResponseDTO> getAllPrescriptions(Pageable pageable) {
        log.info("Fetching all prescriptions (paginated) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return prescriptionRepository.findAll(pageable)
                .map(mapper::entityToResponseDTO);
    }

    // =========================================================
    // GET PRESCRIPTIONS BY DOCTOR ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> getPrescriptionsByDoctorId(Long doctorId) {

        validateId(doctorId, "Doctor ID");

        log.info("Fetching prescriptions for doctor ID={}", doctorId);

        return prescriptionRepository.findByDoctorIdAndIsActiveTrue(doctorId)
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET PRESCRIPTIONS BY PATIENT ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> getPrescriptionsByPatientId(Long patientId) {

        validateId(patientId, "Patient ID");

        log.info("Fetching prescriptions for patient ID={}", patientId);

        return prescriptionRepository.findByPatientIdAndIsActiveTrue(patientId)
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET PRESCRIPTIONS BY STATUS
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> getPrescriptionsByStatus(PrescriptionStatus status) {

        if (status == null)
            throw new BadRequestException("Status cannot be null");

        log.info("Fetching prescriptions with status={}", status);

        return prescriptionRepository.findByStatusAndIsActiveTrue(status)
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    // =========================================================
    // UPDATE PRESCRIPTION
    // =========================================================
    @Override
    public PrescriptionResponseDTO updatePrescription(Long prescriptionId, PrescriptionRequestDTO dto) {

        validateId(prescriptionId, "Prescription ID");

        if (dto == null)
            throw new BadRequestException("Request body cannot be null");

        Prescription prescription = prescriptionRepository.findByPrescriptionIdAndIsActiveTrue(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        log.info("Updating prescription ID={}", prescriptionId);

        // Re-validate doctor and patient if IDs changed
        if (!prescription.getDoctorId().equals(dto.getDoctorId())) {
            String doctorName = fetchDoctorName(dto.getDoctorId());
            prescription.setDoctorId(dto.getDoctorId());
            prescription.setDoctorName(doctorName);
        }

        if (!prescription.getPatientId().equals(dto.getPatientId())) {
            String patientName = fetchPatientName(dto.getPatientId());
            prescription.setPatientId(dto.getPatientId());
            prescription.setPatientName(patientName);
        }

        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setNotes(dto.getNotes());

        if(prescription.getMedications() == null) {
            prescription.setMedications(new ArrayList<>());
        }

        // Update medications
        prescription.clearMedications();
        if (dto.getMedications() != null) {
            for (MedicationDTO medDTO : dto.getMedications()) {
                Medication medication = mapper.medicationDTOtoEntity(medDTO);
                prescription.addMedication(medication);
            }
        }

        Prescription updated = prescriptionRepository.save(prescription);
        log.info("Prescription updated successfully ID={}", prescriptionId);

        return mapper.entityToResponseDTO(updated);
    }

    // =========================================================
    // UPDATE PRESCRIPTION STATUS
    // =========================================================
    @Override
    public PrescriptionResponseDTO updatePrescriptionStatus(Long prescriptionId, PrescriptionStatus status) {

        validateId(prescriptionId, "Prescription ID");

        if (status == null)
            throw new BadRequestException("Status cannot be null");

        Prescription prescription = prescriptionRepository.findByPrescriptionIdAndIsActiveTrue(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        log.info("Updating prescription ID={} status from {} to {}", prescriptionId, prescription.getStatus(), status);

        prescription.setStatus(status);
        Prescription updated = prescriptionRepository.save(prescription);

        return mapper.entityToResponseDTO(updated);
    }

    // =========================================================
    // SOFT DELETE PRESCRIPTION
    // =========================================================
    @Override
    public void deletePrescription(Long prescriptionId) {

        validateId(prescriptionId, "Prescription ID");

        Prescription prescription = prescriptionRepository.findByPrescriptionIdAndIsActiveTrue(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        prescription.setIsActive(false);
        prescriptionRepository.save(prescription);

        log.warn("Prescription SOFT DELETED (deactivated). ID={}", prescriptionId);
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0)
            throw new BadRequestException(fieldName + " must be a positive number");
    }

    @SuppressWarnings("unchecked")
    private String fetchDoctorName(Long doctorId) {
        try {
            Map<String, Object> response = doctorServiceClient.getDoctorById(doctorId);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return (String) data.get("doctorName");
        } catch (Exception e) {
            log.error("Failed to fetch doctor with ID={}: {}", doctorId, e.getMessage());
            throw new ServiceUnavailableException("Doctor Service is unavailable or doctor not found with ID: " + doctorId);
        }
    }

    @SuppressWarnings("unchecked")
    private String fetchPatientName(Long patientId) {
        try {
            Map<String, Object> response = patientServiceClient.getPatientById(patientId);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return (String) data.get("patientName");
        } catch (Exception e) {
            log.error("Failed to fetch patient with ID={}: {}", patientId, e.getMessage());
            throw new ServiceUnavailableException("Patient Service is unavailable or patient not found with ID: " + patientId);
        }
    }
}

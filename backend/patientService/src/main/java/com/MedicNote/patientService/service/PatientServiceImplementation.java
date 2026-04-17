package com.MedicNote.patientService.service;

import com.MedicNote.patientService.entity.Patient;
import com.MedicNote.patientService.dto.PatientRequestDTO;
import com.MedicNote.patientService.dto.PatientResponseDTO;
import com.MedicNote.patientService.mapper.DTOMapper;
import com.MedicNote.patientService.repository.PatientRepository;
import com.MedicNote.patientService.exception.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PatientServiceImplementation implements PatientService {

    private final PatientRepository patientRepository;
    private final DTOMapper mapper;
    private final PasswordEncoder passwordEncoder;

    // =========================================================
    // REGISTER PATIENT
    // =========================================================
    @Override
    public PatientResponseDTO registerPatient(PatientRequestDTO dto) {

        if (dto == null)
            throw new BadRequestException("Request body cannot be null");

        String email = normalizeEmail(dto.getPatientEmail());
        String name = requireText(dto.getPatientName(), "Patient name");
        String password = requireText(dto.getPatientPassword(), "Password");

        log.info("Register request for patient email: {}", email);

        if (patientRepository.existsByPatientEmail(email))
            throw new PatientAlreadyExistsException("Patient already exists with email: " + email);

        if (dto.getPatientPhone() != null &&
                patientRepository.existsByPatientPhone(dto.getPatientPhone()))
            throw new PatientAlreadyExistsException("Patient already exists with phone: " + dto.getPatientPhone());

        Patient patient = mapper.requestDTOtoEntity(dto);
        patient.setPatientEmail(email);
        patient.setPatientName(name);
        patient.setPatientPassword(passwordEncoder.encode(password));
        patient.setIsActive(true);

        Patient saved = patientRepository.save(patient);
        log.info("Patient registered successfully. ID={}", saved.getPatientId());

        return mapper.entityToResponseDTO(saved);
    }

    // =========================================================
    // LOGIN PATIENT
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO loginPatient(String identifier, String password) {

        if (identifier == null || identifier.isBlank())
            throw new BadRequestException("Email or phone cannot be blank");

        if (password == null || password.isBlank())
            throw new BadRequestException("Password cannot be blank");

        String trimmed = identifier.trim();
        log.info("Patient Login attempt with identifier: {}", trimmed);

        // Detect if identifier is email or phone
        boolean isEmail = trimmed.contains("@");
        log.info("Identifier type: {}", isEmail ? "EMAIL" : "PHONE");

        Patient patient;
        if (isEmail) {
            patient = patientRepository.findByPatientEmail(trimmed.toLowerCase())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        } else {
            patient = patientRepository.findByPatientPhone(trimmed)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid phone or password"));
        }

        if (!patient.getIsActive())
            throw new InvalidCredentialsException("Account is inactive");

        if (!passwordEncoder.matches(password, patient.getPatientPassword()))
            throw new InvalidCredentialsException("Invalid Credentials");

        log.info("Patient login success. ID={}", patient.getPatientId());
        return mapper.entityToResponseDTO(patient);
    }

    // =========================================================
    // GET ALL ACTIVE PATIENTS
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getAllPatients() {

        log.info("Fetching all ACTIVE patients");

        return patientRepository.findByIsActiveTrue()
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET ALL ACTIVE PATIENTS (PAGINATED)
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> getAllPatients(Pageable pageable) {

        log.info("Fetching all ACTIVE patients (paginated) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return patientRepository.findByIsActiveTrue(pageable)
                .map(mapper::entityToResponseDTO);
    }

    // =========================================================
    // GET PATIENT BY ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientById(Long patientId) {

        validateId(patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        if (!patient.getIsActive())
            throw new PatientNotFoundException(patientId);

        return mapper.entityToResponseDTO(patient);
    }

    // =========================================================
    // UPDATE PATIENT
    // =========================================================
    @Override
    public PatientResponseDTO updatePatient(Long patientId, PatientRequestDTO dto) {

        validateId(patientId);

        if (dto == null)
            throw new BadRequestException("Request body cannot be null");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        if (!patient.getIsActive())
            throw new PatientNotFoundException(patientId);

        String newEmail = normalizeEmail(dto.getPatientEmail());

        if (!patient.getPatientEmail().equals(newEmail) &&
                patientRepository.existsByPatientEmail(newEmail))
            throw new PatientAlreadyExistsException("Email already in use: " + newEmail);

        if (dto.getPatientPhone() != null &&
                !dto.getPatientPhone().equals(patient.getPatientPhone()) &&
                patientRepository.existsByPatientPhone(dto.getPatientPhone()))
            throw new PatientAlreadyExistsException("Phone already in use");

        patient.setPatientName(requireText(dto.getPatientName(), "Patient name"));
        patient.setPatientEmail(newEmail);
        patient.setPatientPhone(dto.getPatientPhone());
        patient.setGender(dto.getGender());
        patient.setDateOfBirth(dto.getDateOfBirth());

        // password update only if provided
        if (dto.getPatientPassword() != null && !dto.getPatientPassword().isBlank()) {
            patient.setPatientPassword(passwordEncoder.encode(dto.getPatientPassword()));
            log.warn("Patient password updated. ID={}", patientId);
        }

        Patient updated = patientRepository.save(patient);
        log.info("Patient updated successfully ID={}", patientId);

        return mapper.entityToResponseDTO(updated);
    }

    // =========================================================
    // SOFT DELETE PATIENT
    // =========================================================
    @Override
    public void deletePatient(Long patientId) {

        validateId(patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        patient.setIsActive(false);
        patientRepository.save(patient);

        log.warn("Patient SOFT DELETED (deactivated). ID={}", patientId);
    }

    // =========================================================
    // OTP TO PATIENT EMAIL
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByEmail(String email) {
        Patient patient = patientRepository.findByPatientEmail(normalizeEmail(email))
                .orElseThrow(() -> new PatientNotFoundException("No doctor found with email: " + email));
        return mapper.entityToResponseDTO(patient);
    }

    // =========================================================
    // OTP TO PATIENT PHONE
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByPhone(String phone) {
        Patient patient = patientRepository.findByPatientPhone(phone.trim())
                .orElseThrow(() -> new PatientNotFoundException("No doctor found with phone: " + phone));
        return mapper.entityToResponseDTO(patient);
    }

    // =========================================================
    // PRIVATE VALIDATION HELPERS
    // =========================================================

    private void validateId(Long id) {
        if (id == null || id <= 0)
            throw new BadRequestException("Invalid ID");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank())
            throw new BadRequestException("Email cannot be blank");
        return email.trim().toLowerCase();
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank())
            throw new BadRequestException(field + " cannot be blank");
        return value.trim();
    }
}
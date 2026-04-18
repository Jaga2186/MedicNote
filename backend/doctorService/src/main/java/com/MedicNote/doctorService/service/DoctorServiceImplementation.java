package com.MedicNote.doctorService.service;

import com.MedicNote.doctorService.entity.Doctor;
import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.dto.DoctorRequestDTO;
import com.MedicNote.doctorService.mapper.DTOMapper;
import com.MedicNote.doctorService.repository.DoctorRepository;
import com.MedicNote.doctorService.exception.BadRequestException;
import com.MedicNote.doctorService.exception.DoctorNotFoundException;
import com.MedicNote.doctorService.exception.DoctorAlreadyExistsException;
import com.MedicNote.doctorService.exception.InvalidCredentialsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImplementation implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DTOMapper mapper;
    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // REGISTER DOCTOR
    // =========================================================
    @Override
    public DoctorResponseDTO registerDoctor(DoctorRequestDTO dto) {

        if (dto == null) throw new BadRequestException("Request body cannot be null");

        String email = normalizeEmail(dto.getDoctorEmail());
        String name = requireText(dto.getDoctorName(), "Doctor name");
        String hospital = requireText(dto.getHospitalName(), "Hospital name");
        String password = requireText(dto.getDoctorPassword(), "Password");

        log.info("Register request for doctor email: {}", email);

        if (doctorRepository.existsByDoctorEmail(email))
            throw new DoctorAlreadyExistsException("Doctor already exists with email: " + email);

        Doctor doctor = mapper.requestDTOtoEntity(dto);
        doctor.setDoctorEmail(email);
        doctor.setDoctorName(name);
        doctor.setHospitalName(hospital);
        doctor.setDoctorPassword(passwordEncoder.encode(password));
        doctor.setIsActive(true);

        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor registered successfully. ID={}", saved.getDoctorId());

        return mapper.entityToResponseDTO(saved);
    }

    // =========================================================
    // LOGIN DOCTOR
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO loginDoctor(String identifier, String password) {

        if (identifier == null || identifier.isBlank())
            throw new BadRequestException("Email or phone cannot be blank");

        if (password == null || password.isBlank())
            throw new BadRequestException("Password cannot be blank");

        String trimmed = identifier.trim();
        log.info("Doctor Login attempt with identifier: {}", trimmed);

        // Detect if identifier is email or phone
        boolean isEmail = trimmed.contains("@");
        log.info("Identifier type: {}", isEmail ? "EMAIL" : "PHONE");

        Doctor doctor;
        if(isEmail) {
            doctor = doctorRepository.findByDoctorEmail(trimmed.toLowerCase())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        } else {
            doctor = doctorRepository.findByDoctorPhone(trimmed)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        }

        if (!doctor.getIsActive())
            throw new InvalidCredentialsException("Account is inactive");

        if (!passwordEncoder.matches(password, doctor.getDoctorPassword()))
            throw new InvalidCredentialsException("Invalid Credentials");

        log.info("Doctor login success. ID={}", doctor.getDoctorId());
        return mapper.entityToResponseDTO(doctor);
    }

    // =========================================================
    // GET ALL DOCTORS
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> getAllDoctors() {

        log.info("Fetching all doctors");

        return doctorRepository.findAll()
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET ALL DOCTORS (PAGINATED)
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponseDTO> getAllDoctors(Pageable pageable) {

        log.info("Fetching all doctors (paginated) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return doctorRepository.findAll(pageable)
                .map(mapper::entityToResponseDTO);
    }

    // =========================================================
    // GET DOCTOR BY ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO getDoctorById(Long doctorId) {

        validateId(doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        return mapper.entityToResponseDTO(doctor);
    }

    // =========================================================
    // GET DOCTOR BY SPECIALIZATION
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> getDoctorBySpecialization(String specialization) {

        String spec = requireText(specialization, "Specialization");

        return doctorRepository.findBySpecializationIgnoreCase(spec)
                .stream()
                .map(mapper::entityToResponseDTO)
                .toList();
    }

    // =========================================================
    // UPDATE DOCTOR
    // =========================================================
    @Override
    public DoctorResponseDTO updateDoctorById(Long doctorId, DoctorRequestDTO dto) {

        validateId(doctorId);
        if (dto == null) throw new BadRequestException("Request body cannot be null");

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        String newEmail = normalizeEmail(dto.getDoctorEmail());

        if (!doctor.getDoctorEmail().equals(newEmail) &&
                doctorRepository.existsByDoctorEmail(newEmail))
            throw new DoctorAlreadyExistsException("Email already in use: " + newEmail);

        doctor.setDoctorName(requireText(dto.getDoctorName(), "Doctor name"));
        doctor.setDoctorEmail(newEmail);
        doctor.setDoctorPhone(dto.getDoctorPhone());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setHospitalName(requireText(dto.getHospitalName(), "Hospital name"));

        // password update only if provided
        if (dto.getDoctorPassword() != null && !dto.getDoctorPassword().isBlank()) {
            doctor.setDoctorPassword(passwordEncoder.encode(dto.getDoctorPassword()));
            log.info("Password updated for doctor {}", doctorId);
        }

        Doctor updated = doctorRepository.save(doctor);
        log.info("Doctor updated successfully ID={}", doctorId);

        return mapper.entityToResponseDTO(updated);
    }

    // =========================================================
    // DELETE DOCTOR
    // =========================================================
    @Override
    public void deleteDoctorById(Long doctorId) {

        validateId(doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        doctor.setIsActive(false);
        doctorRepository.save(doctor);

        log.info("Doctor deleted ID={}", doctorId);
    }

    // =========================================================
    // OTP TO DOCTOR EMAIL
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByDoctorEmail(normalizeEmail(email))
                .orElseThrow(() -> new DoctorNotFoundException("No doctor found with email: " + email));
        return mapper.entityToResponseDTO(doctor);
    }

    // =========================================================
    // OTP TO DOCTOR PHONE
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO getDoctorByPhone(String phone) {
        Doctor doctor = doctorRepository.findByDoctorPhone(phone.trim())
                .orElseThrow(() -> new DoctorNotFoundException("No doctor found with phone: " + phone));
        return mapper.entityToResponseDTO(doctor);
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
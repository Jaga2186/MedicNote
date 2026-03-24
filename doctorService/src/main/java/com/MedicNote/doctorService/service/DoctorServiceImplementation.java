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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImplementation implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DTOMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public DoctorResponseDTO registerDoctor(DoctorRequestDTO doctorRequestDTO) {

        String email = doctorRequestDTO.getDoctorEmail().trim().toLowerCase();
        
        if(email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        
        log.info("Register request received for email: {}", email);

        if(doctorRepository.existsByDoctorEmail(email)) {
            log.warn("Registration failed - email already exists: {} ", email);
            throw new DoctorAlreadyExistsException("Doctor already exists");
        }

        Doctor doctor = mapper.requestDTOtoEntity(doctorRequestDTO);

        doctor.setDoctorEmail(email);
        doctor.setDoctorName(doctorRequestDTO.getDoctorName().trim());
        doctor.setHospitalName(doctorRequestDTO.getHospitalName().trim());
        doctor.setDoctorPassword(passwordEncoder.encode(doctorRequestDTO.getDoctorPassword()));
        doctor.setIsActive(true);

        Doctor savedDoctor = doctorRepository.save(doctor);

        log.info("Doctor registered successfully with ID: {}", savedDoctor.getDoctorId());

        return mapper.entityToResponseDTO(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO loginDoctor(String email, String password) {
        String normalizedEmail =  email.trim().toLowerCase();
        log.info("Login attempt for email: {}", normalizedEmail);

        Doctor doctor = doctorRepository.findByDoctorEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warn("Login failed - doctor not found for email: {}", normalizedEmail);
                    return new DoctorNotFoundException(normalizedEmail);
                });

        if(!doctor.getIsActive()) {
            log.warn("Login failed - account inactive for email: {}", normalizedEmail);
            throw new InvalidCredentialsException("Account is inactive");
        }

        if(!passwordEncoder.matches(password, doctor.getDoctorPassword())) {
            log.warn("Login failed - invalid credential for email: {}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("Login successful for doctor ID: {}", doctor.getDoctorId());

        return mapper.entityToResponseDTO(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> getAllDoctors() {

        log.info("Fetching all doctors");

        List<Doctor> doctors = doctorRepository.findAll();

        log.info("Found {} doctors", doctors.size());

        return doctors.stream().map(mapper::entityToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO getDoctorById(Long doctorId) {

        log.info("Fetching doctor with Id: {}", doctorId);

        if(doctorId == null) {
            log.error("Doctor ID is null");
            throw new BadRequestException("Doctor id cannot be null");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.warn("Doctor not found with ID: {}", doctorId);
                    return new DoctorNotFoundException(doctorId);
                });

        log.info("Doctor found with ID: {}", doctorId);

        return mapper.entityToResponseDTO(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> getDoctorBySpecialization(String specialization) {

        log.info("Fetching doctors by specialization: {}", specialization);

        if(specialization == null || specialization.isBlank()) {
            log.error("Specialization is null or blank");
            throw new BadRequestException("Specialization cannot be null or blank");
        }

        String cleaned = specialization.trim();

        List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(cleaned);

        log.info("Found {} doctors with specialization {}", doctors.size(), cleaned);

        return doctors.stream().map(mapper::entityToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public DoctorResponseDTO updateDoctorById(Long doctorId, DoctorRequestDTO doctorRequestDTO) {

        log.info("Updating doctor with Id: {}", doctorId);

        if(doctorId == null) {
            log.error("Doctor ID is null");
            throw new BadRequestException("Doctor id cannot be null");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(()-> {
                    log.warn("Update failed - doctor not found with ID: {}", doctorId);
                    return new DoctorNotFoundException(doctorId);
                });

        String normalizedEmail = doctorRequestDTO.getDoctorEmail().trim().toLowerCase();

        if(!doctor.getDoctorEmail().equals(normalizedEmail) &&
            doctorRepository.existsByDoctorEmail(normalizedEmail)) {
            log.info("Update failed - email already exists: {}", normalizedEmail);
            throw new DoctorAlreadyExistsException("Email already exists");
        }

        doctor.setDoctorName(doctorRequestDTO.getDoctorName().trim());
        doctor.setDoctorEmail(normalizedEmail);
        doctor.setDoctorPhone(doctorRequestDTO.getDoctorPhone());
        doctor.setSpecialization(doctorRequestDTO.getSpecialization());
        doctor.setLicenseNumber(doctorRequestDTO.getLicenseNumber());
        doctor.setExperienceYears(doctorRequestDTO.getExperienceYears());
        doctor.setHospitalName(doctorRequestDTO.getHospitalName().trim());

        if(doctorRequestDTO.getDoctorPassword() != null && !doctorRequestDTO.getDoctorPassword().isBlank()) {
            doctor.setDoctorPassword(passwordEncoder.encode(doctorRequestDTO.getDoctorPassword()));
            log.info("Password updated for doctor ID: {}", doctorId);
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);

        log.info("Doctor updated successfully with Id: {}", doctorId);

        return mapper.entityToResponseDTO(updatedDoctor);
    }

    @Override
    @Transactional
    public void deleteDoctorById(Long doctorId) {

        log.info("Deleting doctor with Id: {}", doctorId);

        if(doctorId == null) {
            log.error("Doctor ID is null");
            throw new BadRequestException("Doctor id cannot be null");
        }

        if(!doctorRepository.existsById(doctorId)) {
            log.warn("Delete failed - doctor not found with ID: {}", doctorId);
            throw new DoctorNotFoundException(doctorId);
        }

        doctorRepository.deleteById(doctorId);

        log.info("Doctor deleted successfully with ID: {}", doctorId);
    }

    @Override
    public Boolean checkByDoctorEmail(String email) {

        String normalizedEmail = email.trim().toLowerCase();
        log.debug("Checking if email exists {}", normalizedEmail);

        return doctorRepository.existsByDoctorEmail(normalizedEmail);
    }
}

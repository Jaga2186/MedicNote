package com.MedicNote.doctorService.service;

import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.dto.DoctorRequestDTO;
import com.MedicNote.doctorService.mapper.DTOMapper;
import com.MedicNote.doctorService.exception.DoctorNotFoundException;
import com.MedicNote.doctorService.exception.DoctorAlreadyExistsException;
import com.MedicNote.doctorService.exception.InvalidCredentialsException;
import com.MedicNote.doctorService.entity.Doctor;
import com.MedicNote.doctorService.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImplementation implements DoctorService {

    private final DoctorRepository doctorRepository;

    private final DTOMapper mapper;

    @Override
    public DoctorResponseDTO registerDoctor(DoctorRequestDTO doctorRequestDTO) {
        log.info("Attempting to register Doctor with email: {}", doctorRequestDTO.getDoctorEmail());

        if(doctorRepository.existsByDoctorEmail(doctorRequestDTO.getDoctorEmail())) {
            log.warn("Doctor with email {} already exists", doctorRequestDTO.getDoctorEmail());
            throw new DoctorAlreadyExistsException(
                    "Doctor with email " + doctorRequestDTO.getDoctorEmail() + "already exists"
            );
        }

        Doctor doctor = mapper.requestDTOtoEntity(doctorRequestDTO);
        doctor.setIsActive(true);

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor with email {} registered successfully", doctor.getDoctorEmail());

        return mapper.entityToResponseDTO(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponseDTO loginDoctor(String email, String password) {
        log.info("Attempting to login with email: {}", email);

        Optional<Doctor> doctor = doctorRepository.findByDoctorEmail(email);

        if(doctor.isEmpty()) {
            log.warn("Login failed: Doctor with  email {} not found", email);
            throw new DoctorNotFoundException("Doctor with  email " + email + " not found");
        }

        if(!doctor.get().getDoctorPassword().equals(password)) {
            log.warn("Login failed: Invalid credentials for email {}", email);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("Doctor with email {} logged in successfully", email);
        return mapper.entityToResponseDTO(doctor.get());
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

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        return mapper.entityToResponseDTO(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> getDoctorBySpecialization(String specialization) {
        log.info("Fetching doctors by specialization: {}", specialization);

        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        log.info("Found {} doctors with specialization {}", doctors.size(), specialization);

        return doctors.stream().map(mapper::entityToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public DoctorResponseDTO updateDoctorById(Long doctorId, DoctorRequestDTO doctorRequestDTO) {
        log.info("Updating doctor with Id: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(()-> new DoctorNotFoundException(doctorId));

        if(!doctor.getDoctorEmail().equals(doctorRequestDTO.getDoctorEmail()) &&
            doctorRepository.existsByDoctorEmail(doctorRequestDTO.getDoctorEmail())) {
            log.info("Update failed: Email {} already exists", doctorRequestDTO.getDoctorEmail());
            throw new DoctorAlreadyExistsException("Doctor with email {}" + doctorRequestDTO.getDoctorEmail()
                    +" already exists");
        }

        doctor.setDoctorName(doctorRequestDTO.getDoctorName());
        doctor.setDoctorEmail(doctorRequestDTO.getDoctorEmail());
        doctor.setDoctorPassword(doctorRequestDTO.getDoctorPassword());
        doctor.setDoctorPhone(doctorRequestDTO.getDoctorPhone());
        doctor.setSpecialization(doctorRequestDTO.getSpecialization());
        doctor.setLicenseNumber(doctorRequestDTO.getLicenseNumber());
        doctor.setExperienceYears(doctorRequestDTO.getExperienceYears());
        doctor.setHospitalName(doctorRequestDTO.getHospitalName());

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully with Id: {}", doctorId);

        return mapper.entityToResponseDTO(updatedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteDoctorById(Long doctorId) {
        log.info("Deleting doctor with Id: {}", doctorId);

        if(!doctorRepository.existsById(doctorId)) {
            log.warn("Delete failed: Doctor with Id {} not found", doctorId);
            throw new DoctorNotFoundException("Doctor with Id " + doctorId + " not found");
        }

        doctorRepository.deleteById(doctorId);
        log.info("Doctor with Id {} deleted successfully", doctorId);
    }

    @Override
    public Boolean checkByDoctorEmail(String email) {
        log.debug("Checking if email exists {}", email);
        return doctorRepository.existsByDoctorEmail(email);
    }
}

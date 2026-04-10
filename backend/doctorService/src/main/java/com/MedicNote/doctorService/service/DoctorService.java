package com.MedicNote.doctorService.service;

import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.dto.DoctorRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    DoctorResponseDTO registerDoctor(DoctorRequestDTO doctorRequestDTO);

    DoctorResponseDTO loginDoctor(String email, String password);

    List<DoctorResponseDTO> getAllDoctors();

    Page<DoctorResponseDTO> getAllDoctors(Pageable pageable);

    DoctorResponseDTO getDoctorById(Long doctorId);

    List<DoctorResponseDTO> getDoctorBySpecialization(String specialization);

    DoctorResponseDTO updateDoctorById(Long doctorId, DoctorRequestDTO doctorRequestDTO);

    void deleteDoctorById(Long doctorId);

    Boolean checkByDoctorEmail(String email);
}

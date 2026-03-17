package com.MedicNote.doctorService.repository;

import com.MedicNote.doctorService.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByIsActiveTrue();
}

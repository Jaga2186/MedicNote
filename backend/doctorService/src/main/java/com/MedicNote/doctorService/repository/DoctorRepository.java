package com.MedicNote.doctorService.repository;

import com.MedicNote.doctorService.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByDoctorEmail(String email);
    Boolean existsByDoctorEmail(String email);
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
}

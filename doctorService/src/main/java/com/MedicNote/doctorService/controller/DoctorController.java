package com.MedicNote.doctorService.controller;

import com.MedicNote.doctorService.dto.LoginRequestDTO;
import com.MedicNote.doctorService.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.dto.DoctorRequestDTO;
import com.MedicNote.doctorService.service.DoctorService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctors")
@Validated
@Slf4j
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final JwtUtility jwtUtility;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRequestDTO doctorRequest) {

        log.info("Register request for email: {}", doctorRequest.getDoctorEmail());

        DoctorResponseDTO response = doctorService.registerDoctor(doctorRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Doctor registered successfully",
                        "data", response
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginDoctor(@Valid @RequestBody LoginRequestDTO request) {

        log.info("Doctor login attempt for email: {}", request.getEmail());

        DoctorResponseDTO doctor = doctorService.loginDoctor(
                request.getEmail().trim().toLowerCase(),
                request.getPassword()
        );

        String token = jwtUtility.generateToken(request.getEmail().trim().toLowerCase());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "data", doctor,
                        "token", token
                )
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllDoctors() {

        log.info("Fetching all doctors");

        List<DoctorResponseDTO> doctors = doctorService.getAllDoctors();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Doctors fetched successfully",
                        "count",  doctors.size(),
                        "data", doctors
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable @Positive(message = "Doctor id must be positive") Long id) {

        log.info("Fetching doctor with ID: {}", id);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Doctors retrieved successfully",
                        "data",  doctorService.getDoctorById(id)
                )
        );
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<?> getDoctorBySpecialization(@PathVariable @NotBlank(message = "Specialization is required") String specialization) {

        log.info("Fetching doctor by specialization: {}", specialization);

        List<DoctorResponseDTO> doctors = doctorService.getDoctorBySpecialization(specialization);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Doctors retrieved successfully",
                        "count", doctors.size(),
                        "data", doctors
                )
        );
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<?> updateDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long doctorId,
            @Valid @RequestBody DoctorRequestDTO doctorRequestDTO) {

        log.info("Updating doctor with ID: {}", doctorId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Doctor updated successfully",
                        "data", doctorService.updateDoctorById(doctorId, doctorRequestDTO)
                )
        );
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<?> deleteDoctorById(@PathVariable @Positive(message = "Doctor id must be positive") Long doctorId) {

        log.info("Deleting doctor with ID: {}", doctorId);

        doctorService.deleteDoctorById(doctorId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Doctor deleted successfully",
                        "doctorId", doctorId
                )
        );
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkDoctorByEmail(@PathVariable @NotBlank(message = "Email is required") String email) {

        log.info("Checking doctor by email: {}", email);

        return ResponseEntity.ok(
                Map.of(
                        "exists", doctorService.checkByDoctorEmail(email)
                )
        );
    }
}

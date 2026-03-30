package com.MedicNote.patientService.controller;

import com.MedicNote.patientService.dto.LoginRequestDTO;
import com.MedicNote.patientService.dto.PatientResponseDTO;
import com.MedicNote.patientService.dto.PatientRequestDTO;
import com.MedicNote.patientService.security.JwtUtility;
import com.MedicNote.patientService.service.PatientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PatientController {

    private final PatientService patientService;
    private final JwtUtility jwtUtility;

    /**
     * Register a new patient
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRequestDTO request) {

        log.info("Register patient request for email: {}", request.getPatientEmail());

        PatientResponseDTO response = patientService.registerPatient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Patient registered successfully",
                        "data", response
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@Valid @RequestBody LoginRequestDTO request) {

        log.info("Patient login attempt for email: {}", request.getEmail());

        PatientResponseDTO patient = patientService.loginPatient(
                request.getEmail().trim().toLowerCase(),
                request.getPassword()
        );

        String token = jwtUtility.generateToken(request.getEmail().trim().toLowerCase());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "data", patient,
                        "token", token
                )
        );
    }

    /**
     * Get patient by ID
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(
            @PathVariable @Positive(message = "Patient id must be positive") Long patientId) {

        log.info("Fetching patient by ID: {}", patientId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Patient retrieved successfully",
                        "data", patientService.getPatientById(patientId)
                )
        );
    }

    /**
     * Get all patients
     */
    @GetMapping
    public ResponseEntity<?> getAllPatients() {

        log.info("Fetching all patients");

        List<PatientResponseDTO> patients = patientService.getAllPatients();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Patients fetched successfully",
                        "count", patients.size(),
                        "data", patients
                )
        );
    }

    /**
     * Update patient by ID
     */
    @PutMapping("/{patientId}")
    public ResponseEntity<?> updatePatientById(
            @PathVariable @Positive(message = "Patient id must be positive") Long patientId,
            @Valid @RequestBody PatientRequestDTO request) {

        log.info("Updating patient ID: {}", patientId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Patient updated successfully",
                        "data", patientService.updatePatient(patientId, request)
                )
        );
    }

    /**
     * Delete patient by ID
     */
    @DeleteMapping("/{patientId}")
    public ResponseEntity<?> deletePatientById(
            @PathVariable @Positive(message = "Patient id must be positive") Long patientId) {

        log.info("Deleting patient ID: {}", patientId);

        patientService.deletePatient(patientId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Patient deleted successfully",
                        "patientId", patientId
                )
        );
    }
}
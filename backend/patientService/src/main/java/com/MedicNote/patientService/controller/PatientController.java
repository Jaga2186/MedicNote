package com.MedicNote.patientService.controller;

import com.MedicNote.patientService.dto.LoginRequestDTO;
import com.MedicNote.patientService.dto.PatientResponseDTO;
import com.MedicNote.patientService.dto.PatientRequestDTO;
import com.MedicNote.patientService.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Patient", description = "Patient management APIs")
public class PatientController {

    private final PatientService patientService;
    // ✅ No JwtUtility here — token generation is Auth Service's responsibility

    @Operation(summary = "Register a new patient")
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRequestDTO request) {
        log.info("Register patient request for email: {}", request.getPatientEmail());
        PatientResponseDTO response = patientService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "Patient registered successfully", "data", response));
    }

    @Operation(summary = "Patient login — credential validation only, JWT issued by Auth Service")
    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Patient login attempt for email: {}", request.getEmail());
        PatientResponseDTO patient = patientService.loginPatient(
                request.getEmail().trim().toLowerCase(), request.getPassword());
        // ✅ Returns data only — Auth Service generates the token
        return ResponseEntity.ok(
                Map.of("message", "Login successful", "data", patient));
    }

    @Operation(summary = "Get patient by ID")
    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(
            @PathVariable @Positive(message = "Patient id must be positive") Long patientId) {
        log.info("Fetching patient by ID: {}", patientId);
        return ResponseEntity.ok(
                Map.of("message", "Patient retrieved successfully", "data", patientService.getPatientById(patientId)));
    }

    @Operation(summary = "Get all patients")
    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        log.info("Fetching all patients");
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(
                Map.of("message", "Patients fetched successfully", "count", patients.size(), "data", patients));
    }

    @Operation(summary = "Get all patients (paginated)")
    @GetMapping("/page")
    public ResponseEntity<?> getAllPatientsPaginated(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "patientId") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PatientResponseDTO> patients = patientService.getAllPatients(pageable);

        return ResponseEntity.ok(Map.of(
                "message", "Patients fetched successfully",
                "data", patients.getContent(),
                "currentPage", patients.getNumber(),
                "totalItems", patients.getTotalElements(),
                "totalPages", patients.getTotalPages()));
    }

    @Operation(summary = "Update patient")
    @PutMapping("/{patientId}")
    public ResponseEntity<?> updatePatientById(
            @PathVariable @Positive(message = "Patient id must be positive") Long patientId,
            @Valid @RequestBody PatientRequestDTO request) {
        log.info("Updating patient ID: {}", patientId);
        return ResponseEntity.ok(
                Map.of("message", "Patient updated successfully", "data", patientService.updatePatient(patientId, request)));
    }

    @Operation(summary = "Delete patient")
    @DeleteMapping("/{patientId}")
    public ResponseEntity<?> deletePatientById(
            @PathVariable @Positive(message = "Patient id must be positive") Long patientId) {
        log.info("Deleting patient ID: {}", patientId);
        patientService.deletePatient(patientId);
        return ResponseEntity.ok(
                Map.of("message", "Patient deleted successfully", "patientId", patientId));
    }
}
package com.MedicNote.doctorService.controller;

import com.MedicNote.doctorService.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@Validated
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Doctor", description = "Doctor management APIs")
public class DoctorController {

    private final DoctorService doctorService;
    // ✅ No JwtUtility here — token generation is Auth Service's responsibility

    @Operation(summary = "Register a new doctor")
    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRequestDTO doctorRequest) {
        log.info("Register request for email: {}", doctorRequest.getDoctorEmail());
        DoctorResponseDTO response = doctorService.registerDoctor(doctorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "Doctor registered successfully", "data", response));
    }

    @Operation(summary = "Doctor login — credential validation only, JWT issued by Auth Service")
    @PostMapping("/login")
    public ResponseEntity<?> loginDoctor(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Doctor login attempt for email: {}", request.getIdentifier());
        DoctorResponseDTO doctor = doctorService.loginDoctor(request.getIdentifier(), request.getPassword());
        // Returns data only — Auth Service generates the token
        return ResponseEntity.ok(
                Map.of("message", "Login successful", "data", doctor));
    }

    @Operation(summary = "Get all doctors")
    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        log.info("Fetching all doctors");
        List<DoctorResponseDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(
                Map.of("message", "Doctors fetched successfully", "count", doctors.size(), "data", doctors));
    }

    @Operation(summary = "Get all doctors (paginated)")
    @GetMapping("/page")
    public ResponseEntity<?> getAllDoctorsPaginated(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "doctorId") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DoctorResponseDTO> doctors = doctorService.getAllDoctors(pageable);

        return ResponseEntity.ok(Map.of(
                "message", "Doctors fetched successfully",
                "data", doctors.getContent(),
                "currentPage", doctors.getNumber(),
                "totalItems", doctors.getTotalElements(),
                "totalPages", doctors.getTotalPages()));
    }

    @Operation(summary = "Get doctor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long id) {
        log.info("Fetching doctor with ID: {}", id);
        return ResponseEntity.ok(
                Map.of("message", "Doctor retrieved successfully", "data", doctorService.getDoctorById(id)));
    }

    @Operation(summary = "Get doctors by specialization")
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<?> getDoctorBySpecialization(
            @PathVariable @NotBlank(message = "Specialization is required") String specialization) {
        log.info("Fetching doctors by specialization: {}", specialization);
        List<DoctorResponseDTO> doctors = doctorService.getDoctorBySpecialization(specialization);
        return ResponseEntity.ok(
                Map.of("message", "Doctors retrieved successfully", "count", doctors.size(), "data", doctors));
    }

    @Operation(summary = "Update doctor")
    @PutMapping("/{doctorId}")
    public ResponseEntity<?> updateDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long doctorId,
            @Valid @RequestBody DoctorRequestDTO doctorRequestDTO) {
        log.info("Updating doctor with ID: {}", doctorId);
        return ResponseEntity.ok(
                Map.of("message", "Doctor updated successfully", "data", doctorService.updateDoctorById(doctorId, doctorRequestDTO)));
    }

    @Operation(summary = "Delete doctor")
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<?> deleteDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long doctorId) {
        log.info("Deleting doctor with ID: {}", doctorId);
        doctorService.deleteDoctorById(doctorId);
        return ResponseEntity.ok(
                Map.of("message", "Doctor deleted successfully", "doctorId", doctorId));
    }

    @Operation(summary = "Check email existence")
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkDoctorByEmail(
            @PathVariable @NotBlank(message = "Email is required") String email) {
        log.info("Checking doctor by email: {}", email);
        return ResponseEntity.ok(Map.of("exists", doctorService.checkByDoctorEmail(email)));
    }


    @Operation(summary = "Get doctor by email")
    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getDoctorByEmail(@PathVariable String email) {
        log.info("Fetching doctor by email: {}", email);
        return ResponseEntity.ok(
                Map.of("message", "Doctor retrieved successfully",
                        "data", doctorService.getDoctorByEmail(email)));
    }

    @Operation(summary = "Get doctor by phone")
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<?> getDoctorByPhone(@PathVariable String phone) {
        log.info("Fetching doctor by phone: {}", phone);
        return ResponseEntity.ok(
                Map.of("message", "Doctor retrieved successfully",
                        "data", doctorService.getDoctorByPhone(phone)));
    }
}
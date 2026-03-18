package com.MedicNote.doctorService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import com.MedicNote.doctorService.dto.DoctorResponseDTO;
import com.MedicNote.doctorService.dto.DoctorRequestDTO;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/doctors")
@Validated
@Slf4j
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerDoctor(@Valid @RequestBody DoctorRequestDTO doctorRequest) {
        log.info("Register doctor endpoint called for email: {}", doctorRequest.getDoctorEmail());

        DoctorResponseDTO doctorResponseDTO = doctorService.registerDoctor(doctorRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Doctor registered successfully");
        result.put("data", doctorResponseDTO);

        log.info("Doctor registered successfully with ID: {}", doctorResponseDTO.getDoctorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginDoctor(
            @RequestParam @NotBlank(message = "Email is required") String email,
            @RequestParam @NotBlank(message = "PassWord is required") String passWord) {
        log.info("Doctor login attempt for email: {}", email);

        DoctorResponseDTO doctorResponseDTO = doctorService.loginDoctor(email, passWord);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Doctor login successful");
        result.put("data", doctorResponseDTO);

        log.info("Doctor login successful with ID: {}", doctorResponseDTO.getDoctorId());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getALlDoctors() {
        log.info("Fetching all doctors");

        List<DoctorResponseDTO> doctors = doctorService.getAllDoctors();

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Doctors fetch successful");
        result.put("data", doctors);
        result.put("count", doctors.size());

        log.info("Total {} doctors retrieved successfully", doctors.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long id) {
        log.info("Fetching doctor with ID: {}", id);
        DoctorResponseDTO response = doctorService.getDoctorById(id);

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Doctor retrieved successfully");
        result.put("data", response);

        log.info("Doctor retrieved successfully with ID: {}", id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<Map<String, Object>> getDoctorBySpecialization(
            @PathVariable @NotBlank(message = "Specialization is required") String specialization) {
        log.info("Fetching doctor by specialization: {}", specialization);

        List<DoctorResponseDTO> doctors = doctorService.getDoctorBySpecialization(specialization);

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Doctor retrieved successfully");
        result.put("data", doctors);
        result.put("count", doctors.size());
        result.put("specialization", specialization);

        log.info("Found {} doctors with specialization: {}", doctors.size(), specialization);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long doctorId,
            @Valid @RequestBody DoctorRequestDTO doctorRequestDTO) {
        log.info("Updating doctor with ID: {}", doctorId);

        DoctorResponseDTO response = doctorService.updateDocterById(doctorId, doctorRequestDTO);

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Doctor updated successfully");
        result.put("data", response);

        log.info("Doctor updated successfully with ID: {}", doctorId);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDoctorById(
            @PathVariable @Positive(message = "Doctor id must be positive") Long doctorId) {
        log.info("Deleting doctor with ID: {}", doctorId);

        doctorService.deleteDoctorById(doctorId);

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Doctor deleted successfully");
        result.put("doctorId", doctorId);

        log.info("Doctor deleted successfully with ID: {}", doctorId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkDoctorByEmail(
            @PathVariable @NotBlank(message = "Email is required") String email) {

        log.info("Checking doctor by email: {}", email);

        Boolean exists = doctorService.checkDoctorByEmail(email);

        Map<String, Object> result = new HashMap<>();

        result.put("message", "Doctor check successful");
        result.put("exists", exists);

        log.info("Doctor check successful with email: {}", email);

        return ResponseEntity.ok(result);
    }
}

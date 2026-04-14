package com.MedicNote.authService.controller;

import com.MedicNote.authService.dto.AuthResponseDTO;
import com.MedicNote.authService.dto.DoctorRegisterRequestDTO;
import com.MedicNote.authService.dto.LoginRequestDTO;
import com.MedicNote.authService.dto.PatientRegisterRequestDTO;
import com.MedicNote.authService.feign.DoctorServiceClient;
import com.MedicNote.authService.feign.PatientServiceClient;
import com.MedicNote.authService.security.JwtUtility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Centralized authentication APIs")
public class AuthController {

    private final DoctorServiceClient doctorServiceClient;
    private final PatientServiceClient patientServiceClient;
    private final JwtUtility jwtUtility;

    // ============ DOCTOR REGISTER ============
    @Operation(summary = "Register a new doctor")
    @PostMapping("/doctor/register")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegisterRequestDTO request) {
        log.info("Auth: Doctor registration request");
        Map<String, Object> response = doctorServiceClient.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============ DOCTOR LOGIN ============
    @Operation(summary = "Doctor login — validates via Doctor Service, issues JWT here")
    @PostMapping("/doctor/login")
    public ResponseEntity<?> loginDoctor(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Auth: Doctor login attempt for email: {}", request.getEmail());

        Map<String, Object> loginRequest = Map.of(
                "email", request.getEmail(),
                "password", request.getPassword()
        );

        // Doctor Service validates credentials and returns doctor data
        Map<String, Object> response = doctorServiceClient.loginDoctor(loginRequest);

        // Auth Service is the ONLY place that issues JWT tokens
        String token = jwtUtility.generateToken(
                request.getEmail().trim().toLowerCase(), "DOCTOR");

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .message("Doctor login successful")
                        .token(token)
                        .role("DOCTOR")
                        .data(response.get("data"))
                        .build()
        );
    }

    // ============ PATIENT REGISTER ============
    @Operation(summary = "Register a new patient")
    @PostMapping("/patient/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegisterRequestDTO request) {
        log.info("Auth: Patient registration request");
        Map<String, Object> response = patientServiceClient.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============ PATIENT LOGIN ============
    @Operation(summary = "Patient login — validates via Patient Service, issues JWT here")
    @PostMapping("/patient/login")
    public ResponseEntity<?> loginPatient(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Auth: Patient login attempt for email: {}", request.getEmail());

        Map<String, Object> loginRequest = Map.of(
                "email", request.getEmail(),
                "password", request.getPassword()
        );

        // Patient Service validates credentials and returns patient data
        Map<String, Object> response = patientServiceClient.loginPatient(loginRequest);

        // Auth Service is the ONLY place that issues JWT tokens
        String token = jwtUtility.generateToken(
                request.getEmail().trim().toLowerCase(), "PATIENT");

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .message("Patient login successful")
                        .token(token)
                        .role("PATIENT")
                        .data(response.get("data"))
                        .build()
        );
    }

    // ============ VALIDATE TOKEN ============
    @Operation(summary = "Validate JWT token")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @Parameter(description = "Bearer token", required = true)
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("valid", false, "message", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        if (jwtUtility.validateToken(token)) {
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "email", jwtUtility.extractEmail(token),
                    "role", jwtUtility.extractRole(token)
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("valid", false, "message", "Token is invalid or expired"));
    }
}
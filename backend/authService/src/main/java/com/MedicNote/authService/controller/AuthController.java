package com.MedicNote.authService.controller;

import com.MedicNote.authService.dto.AuthResponseDTO;
import com.MedicNote.authService.dto.DoctorRegisterRequestDTO;
import com.MedicNote.authService.dto.LoginRequestDTO;
import com.MedicNote.authService.dto.OtpVerifyRequestDTO;
import com.MedicNote.authService.dto.PatientRegisterRequestDTO;
import com.MedicNote.authService.feign.DoctorServiceClient;
import com.MedicNote.authService.feign.PatientServiceClient;
import com.MedicNote.authService.security.JwtUtility;
import com.MedicNote.authService.service.OtpService;

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
    private final OtpService otpService;

    // ============ DOCTOR REGISTER ============
    @Operation(summary = "Register a new doctor")
    @PostMapping("/doctor/register")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegisterRequestDTO request) {
        log.info("Auth: Doctor registration request");
        Map<String, Object> response = doctorServiceClient.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============ DOCTOR LOGIN — Step 1 ============
    @Operation(summary = "Doctor login — validates credentials, sends OTP to registered email")
    @PostMapping("/doctor/login")
    public ResponseEntity<?> loginDoctor(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Auth: Doctor login attempt for identifier: {}", request.getIdentifier());

        Map<String, Object> loginRequest = Map.of(
                "identifier", request.getIdentifier(),
                "password", request.getPassword()
        );

        // Step 1: Validate credentials via Doctor Service
        Map<String, Object> response = doctorServiceClient.loginDoctor(loginRequest);
        Map<String, Object> doctorData = (Map<String, Object>) response.get("data");
        String email = (String) doctorData.get("doctorEmail");

        // Step 2: Generate session + send OTP to email
        String sessionToken = otpService.createSessionAndSendOtp(email, "DOCTOR");
        log.info("OTP sent to doctor email: {}", email);

        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to your registered email " + otpService.maskEmail(email),
                "sessionToken", sessionToken
        ));
    }

    // ============ PATIENT REGISTER ============
    @Operation(summary = "Register a new patient")
    @PostMapping("/patient/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegisterRequestDTO request) {
        log.info("Auth: Patient registration request");
        Map<String, Object> response = patientServiceClient.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============ PATIENT LOGIN — Step 1 ============
    @Operation(summary = "Patient login — validates credentials, sends OTP to registered email")
    @PostMapping("/patient/login")
    public ResponseEntity<?> loginPatient(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Auth: Patient login attempt for identifier: {}", request.getIdentifier());

        Map<String, Object> loginRequest = Map.of(
                "identifier", request.getIdentifier(),
                "password", request.getPassword()
        );

        // Step 1: Validate credentials via Patient Service
        Map<String, Object> response = patientServiceClient.loginPatient(loginRequest);
        Map<String, Object> patientData = (Map<String, Object>) response.get("data");
        String email = (String) patientData.get("patientEmail");

        // Step 2: Generate session + send OTP to email
        String sessionToken = otpService.createSessionAndSendOtp(email, "PATIENT");
        log.info("OTP sent to patient email: {}", email);

        return ResponseEntity.ok(Map.of(
                "message", "OTP sent to your registered email " + otpService.maskEmail(email),
                "sessionToken", sessionToken
        ));
    }

    // ============ VERIFY OTP — Step 2 ============
    @Operation(summary = "Verify OTP and get JWT token")
    @PostMapping("/otp/verify")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@Valid @RequestBody OtpVerifyRequestDTO request) {
        log.info("Auth: OTP verification attempt");
        AuthResponseDTO authResponse = otpService.verifyOtp(
                request.getSessionToken(), request.getOtpCode());
        return ResponseEntity.ok(authResponse);
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

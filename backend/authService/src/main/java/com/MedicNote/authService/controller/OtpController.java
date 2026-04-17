package com.MedicNote.authService.controller;

import com.MedicNote.authService.dto.AuthResponseDTO;
import com.MedicNote.authService.dto.OtpRequestDTO;
import com.MedicNote.authService.dto.OtpVerifyRequestDTO;
import com.MedicNote.authService.service.OtpService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OTP Authentication", description = "OTP-based login APIs")
public class OtpController {

    private final OtpService otpService;

    @Operation(summary = "Send OTP to registered email")
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequestDTO request) {
        log.info("OTP send request for identifier: {}, role: {}", request.getIdentifier(), request.getRole());
        String maskedEmail = otpService.sendOtp(request.getIdentifier(), request.getRole());
        return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully to " + maskEmail(maskedEmail),
                "email", maskEmail(maskedEmail)
        ));
    }

    @Operation(summary = "Verify OTP and get JWT token")
    @PostMapping("/verify")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@Valid @RequestBody OtpVerifyRequestDTO request) {
        log.info("OTP verify request for identifier: {}, role: {}", request.getIdentifier(), request.getRole());
        AuthResponseDTO response = otpService.verifyOtp(
                request.getIdentifier(), request.getRole(), request.getOtpCode());
        return ResponseEntity.ok(response);
    }

    // Mask email for privacy: john.smith@hospital.com → j***h@hospital.com
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;
        return email.charAt(0) + "***" + email.charAt(atIndex - 1) + email.substring(atIndex);
    }
}

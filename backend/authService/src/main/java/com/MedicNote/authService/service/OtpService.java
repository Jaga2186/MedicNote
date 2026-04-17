package com.MedicNote.authService.service;

import com.MedicNote.authService.dto.AuthResponseDTO;
import com.MedicNote.authService.entity.OtpRecord;
import com.MedicNote.authService.exception.DownstreamServiceException;
import com.MedicNote.authService.feign.DoctorServiceClient;
import com.MedicNote.authService.feign.PatientServiceClient;
import com.MedicNote.authService.repository.OtpRepository;
import com.MedicNote.authService.security.JwtUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final DoctorServiceClient doctorServiceClient;
    private final PatientServiceClient patientServiceClient;
    private final JwtUtility jwtUtility;

    @Value("${otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    // =========================================================
    // SEND OTP
    // =========================================================
    @Transactional
    public String sendOtp(String identifier, String role) {

        log.info("OTP send request — identifier: {}, role: {}", identifier, role);

        // Step 1: Validate role
        validateRole(role);

        // Step 2: Fetch user email via Feign based on role and identifier
        String email = resolveEmail(identifier, role);
        log.info("Resolved email for OTP: {} (identifier: {})", email, identifier);

        // Step 3: Generate 6-digit OTP
        String otpCode = generateOtp();
        log.info("Generated OTP for email: {} (not logging OTP value in prod)", email);

        // Step 4: Invalidate any previous unused OTPs for this email
        // (we just let them expire — new one overwrites by checking latest)

        // Step 5: Save OTP record
        OtpRecord record = OtpRecord.builder()
                .email(email)
                .identifier(identifier.trim())
                .role(role.toUpperCase())
                .otpCode(otpCode)
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .build();

        otpRepository.save(record);
        log.info("OTP record saved for email: {}", email);

        // Step 6: Send email
        emailService.sendOtpEmail(email, otpCode, role);

        return email; // return masked email to show on frontend
    }

    // =========================================================
    // VERIFY OTP + ISSUE JWT
    // =========================================================
    @Transactional
    public AuthResponseDTO verifyOtp(String identifier, String role, String otpCode) {

        log.info("OTP verify request — identifier: {}, role: {}", identifier, role);

        validateRole(role);

        // Step 1: Resolve email from identifier
        String email = resolveEmail(identifier, role);

        // Step 2: Find latest valid OTP for this email
        OtpRecord record = otpRepository
                .findTopByEmailAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        email, LocalDateTime.now())
                .orElseThrow(() -> {
                    log.warn("No valid OTP found for email: {}", email);
                    return new DownstreamServiceException(401, "OTP expired or not found. Please request a new one.", null);
                });

        // Step 3: Validate OTP code
        if (!record.getOtpCode().equals(otpCode)) {
            log.warn("Invalid OTP attempt for email: {}", email);
            throw new DownstreamServiceException(401, "Invalid OTP. Please try again.", null);
        }

        // Step 4: Mark OTP as used
        record.setIsUsed(true);
        otpRepository.save(record);
        log.info("OTP verified and marked as used for email: {}", email);

        // Step 5: Fetch user data from downstream service
        Map<String, Object> userData = fetchUserData(identifier, role);

        // Step 6: Generate JWT
        String token = jwtUtility.generateToken(email.trim().toLowerCase(), role.toUpperCase());
        log.info("JWT issued for email: {}, role: {}", email, role);

        return AuthResponseDTO.builder()
                .message("OTP login successful")
                .token(token)
                .role(role.toUpperCase())
                .data(userData.get("data"))
                .build();
    }

    // =========================================================
    // SCHEDULED CLEANUP — runs every hour
    // =========================================================
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredOtps() {
        log.info("Running OTP cleanup job...");
        otpRepository.deleteExpiredAndUsedOtps(LocalDateTime.now());
        log.info("OTP cleanup complete");
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private String resolveEmail(String identifier, String role) {
        boolean isEmail = identifier.contains("@");

        try {
            if (role.equalsIgnoreCase("DOCTOR")) {
                Map<String, Object> response = isEmail
                        ? doctorServiceClient.getDoctorByEmail(identifier.trim().toLowerCase())
                        : doctorServiceClient.getDoctorByPhone(identifier.trim());

                Map<String, Object> data = (Map<String, Object>) response.get("data");
                return (String) data.get("doctorEmail");

            } else {
                Map<String, Object> response = isEmail
                        ? patientServiceClient.getPatientByEmail(identifier.trim().toLowerCase())
                        : patientServiceClient.getPatientByPhone(identifier.trim());

                Map<String, Object> data = (Map<String, Object>) response.get("data");
                return (String) data.get("patientEmail");
            }
        } catch (DownstreamServiceException e) {
            log.error("User not found for identifier: {}, role: {}", identifier, role);
            throw new DownstreamServiceException(404,
                    "No account found for the provided " + (identifier.contains("@") ? "email" : "phone") + ".", e);
        }
    }

    private Map<String, Object> fetchUserData(String identifier, String role) {
        boolean isEmail = identifier.contains("@");
        if (role.equalsIgnoreCase("DOCTOR")) {
            return isEmail
                    ? doctorServiceClient.getDoctorByEmail(identifier.trim().toLowerCase())
                    : doctorServiceClient.getDoctorByPhone(identifier.trim());
        } else {
            return isEmail
                    ? patientServiceClient.getPatientByEmail(identifier.trim().toLowerCase())
                    : patientServiceClient.getPatientByPhone(identifier.trim());
        }
    }

    private void validateRole(String role) {
        if (!role.equalsIgnoreCase("DOCTOR") && !role.equalsIgnoreCase("PATIENT")) {
            throw new DownstreamServiceException(400, "Invalid role. Must be DOCTOR or PATIENT.", null);
        }
    }

    private String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
}
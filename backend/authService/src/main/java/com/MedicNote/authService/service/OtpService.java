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
import java.util.UUID;

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

    @Value("${otp.session-expiry-minutes:10}")
    private int sessionExpiryMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    // =========================================================
    // Called from AuthController after password is validated
    // =========================================================
    @Transactional
    public String createSessionAndSendOtp(String email, String role) {

        String otpCode = generateOtp();
        String sessionToken = UUID.randomUUID().toString();

        OtpRecord record = OtpRecord.builder()
                .sessionToken(sessionToken)
                .email(email.trim().toLowerCase())
                .role(role.toUpperCase())
                .otpCode(otpCode)
                .isUsed(false)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .sessionExpiresAt(LocalDateTime.now().plusMinutes(sessionExpiryMinutes))
                .build();

        otpRepository.save(record);
        log.info("OTP session created for email: {}, role: {}", email, role);

        emailService.sendOtpEmail(email, otpCode, role);

        return sessionToken;
    }

    // =========================================================
    // VERIFY OTP — issues JWT
    // =========================================================
    @Transactional
    public AuthResponseDTO verifyOtp(String sessionToken, String otpCode) {

        log.info("Verifying OTP for session: {}", sessionToken);

        // Find session
        OtpRecord record = otpRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new DownstreamServiceException(401,
                        "Invalid or expired session. Please login again.", null));

        // Check session expiry
        if (LocalDateTime.now().isAfter(record.getSessionExpiresAt())) {
            throw new DownstreamServiceException(401,
                    "Session expired. Please login again.", null);
        }

        // Check already used
        if (record.getIsUsed()) {
            throw new DownstreamServiceException(401,
                    "OTP already used. Please login again.", null);
        }

        // Check OTP expiry
        if (LocalDateTime.now().isAfter(record.getExpiresAt())) {
            throw new DownstreamServiceException(401,
                    "OTP has expired. Please login again.", null);
        }

        // Validate OTP code
        if (!record.getOtpCode().equals(otpCode)) {
            log.warn("Invalid OTP attempt for email: {}", record.getEmail());
            throw new DownstreamServiceException(401,
                    "Invalid OTP. Please try again.", null);
        }

        // Mark as used
        record.setIsUsed(true);
        otpRepository.save(record);
        log.info("OTP verified — email: {}, role: {}", record.getEmail(), record.getRole());

        // Fetch user data from downstream service
        Map<String, Object> userData = fetchUserData(record.getEmail(), record.getRole());

        // Issue JWT
        String token = jwtUtility.generateToken(record.getEmail(), record.getRole());
        log.info("JWT issued — email: {}, role: {}", record.getEmail(), record.getRole());

        return AuthResponseDTO.builder()
                .message(record.getRole().equals("DOCTOR")
                        ? "Doctor login successful"
                        : "Patient login successful")
                .token(token)
                .role(record.getRole())
                .data(userData.get("data"))
                .build();
    }

    // =========================================================
    // SCHEDULED CLEANUP — every hour
    // =========================================================
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredOtps() {
        log.info("Running OTP cleanup job...");
        otpRepository.deleteExpiredAndUsedOtps(LocalDateTime.now());
        log.info("OTP cleanup complete");
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Map<String, Object> fetchUserData(String email, String role) {
        if (role.equalsIgnoreCase("DOCTOR")) {
            return doctorServiceClient.getDoctorByEmail(email);
        } else {
            return patientServiceClient.getPatientByEmail(email);
        }
    }

    public String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 2) return email;
        return email.charAt(0) + "***" + email.charAt(at - 1) + email.substring(at);
    }

    private String generateOtp() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}

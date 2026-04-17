package com.MedicNote.authService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("MedicNote — Your OTP Code");
            helper.setText(buildEmailBody(otpCode, role), true);

            mailSender.send(message);
            log.info("OTP email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }
    }

    private String buildEmailBody(String otpCode, String role) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 30px;
                            border: 1px solid #e0e0e0; border-radius: 8px;">
                    <h2 style="color: #2563EB;">MedicNote</h2>
                    <p>Hello,</p>
                    <p>Your OTP code for <strong>%s</strong> login is:</p>
                    <div style="font-size: 36px; font-weight: bold; letter-spacing: 8px;
                                color: #2563EB; text-align: center; padding: 20px 0;">
                        %s
                    </div>
                    <p style="color: #666;">This OTP is valid for <strong>5 minutes</strong>. Do not share it with anyone.</p>
                    <p style="color: #999; font-size: 12px;">If you did not request this, please ignore this email.</p>
                </div>
                """.formatted(role, otpCode);
    }
}

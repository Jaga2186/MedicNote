package com.MedicNote.prescriptionService.service;

import com.MedicNote.prescriptionService.entity.Prescription;
import com.MedicNote.prescriptionService.exception.ServiceUnavailableException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    @Value("${spring.mail.username:noreply@medicnote.com}")
    private String fromEmail;

    public void sendPrescriptionEmail(Prescription prescription, String patientEmail) {

        log.info("Sending prescription email for ID={} to {}", prescription.getPrescriptionId(), patientEmail);

        try {
            byte[] pdfBytes = pdfService.generatePrescriptionPdf(prescription);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(patientEmail);
            helper.setSubject("MedicNote - Your Prescription #" + prescription.getPrescriptionId());

            String body = """
                    <html>
                    <body style="font-family: Arial, sans-serif;">
                        <h2 style="color: #006699;">MedicNote - Digital Prescription</h2>
                        <p>Dear %s,</p>
                        <p>Your prescription has been created by <strong>Dr. %s</strong>.</p>
                        <p><strong>Diagnosis:</strong> %s</p>
                        <p>Please find your prescription attached as a PDF document.</p>
                        <br/>
                        <p style="color: gray; font-size: 12px;">
                            This is an automated email from MedicNote. Please do not reply to this email.
                        </p>
                    </body>
                    </html>
                    """.formatted(
                    prescription.getPatientName(),
                    prescription.getDoctorName(),
                    prescription.getDiagnosis()
            );

            helper.setText(body, true);

            String fileName = "Prescription_" + prescription.getPrescriptionId() + ".pdf";
            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes), "application/pdf");

            mailSender.send(message);

            log.info("Prescription email sent successfully for ID={}", prescription.getPrescriptionId());

        } catch (MessagingException e) {
            log.error("Failed to send prescription email for ID={}: {}", prescription.getPrescriptionId(), e.getMessage());
            throw new ServiceUnavailableException("Failed to send email: " + e.getMessage());
        }
    }
}

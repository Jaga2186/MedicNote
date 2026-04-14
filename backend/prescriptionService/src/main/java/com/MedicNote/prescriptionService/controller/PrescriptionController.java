package com.MedicNote.prescriptionService.controller;

import com.MedicNote.prescriptionService.dto.PrescriptionRequestDTO;
import com.MedicNote.prescriptionService.dto.PrescriptionResponseDTO;
import com.MedicNote.prescriptionService.entity.Prescription;
import com.MedicNote.prescriptionService.entity.PrescriptionStatus;
import com.MedicNote.prescriptionService.exception.BadRequestException;
import com.MedicNote.prescriptionService.exception.PrescriptionNotFoundException;
import com.MedicNote.prescriptionService.exception.ServiceUnavailableException;
import com.MedicNote.prescriptionService.feign.PatientServiceClient;
import com.MedicNote.prescriptionService.repository.PrescriptionRepository;
import com.MedicNote.prescriptionService.service.EmailService;
import com.MedicNote.prescriptionService.service.PdfService;
import com.MedicNote.prescriptionService.service.PrescriptionService;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Slf4j
@Validated
@EnableMethodSecurity   // enables @PreAuthorize
@Tag(name = "Prescription", description = "Prescription management APIs")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PdfService pdfService;
    private final EmailService emailService;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientServiceClient patientServiceClient;

    // ============================================
    // DOCTOR ONLY — Create prescription
    // ============================================
    @Operation(summary = "Create prescription (DOCTOR only)")
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createPrescription(@Valid @RequestBody PrescriptionRequestDTO request) {
        log.info("Create prescription request for doctor={} patient={}", request.getDoctorId(), request.getPatientId());
        PrescriptionResponseDTO response = prescriptionService.createPrescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "Prescription created successfully", "data", response));
    }

    // ============================================
    // DOCTOR ONLY — Update prescription
    // ============================================
    @Operation(summary = "Update prescription (DOCTOR only)")
    @PutMapping("/{prescriptionId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updatePrescription(
            @PathVariable @Positive(message = "Prescription ID must be positive") Long prescriptionId,
            @Valid @RequestBody PrescriptionRequestDTO request) {
        log.info("Updating prescription ID: {}", prescriptionId);
        return ResponseEntity.ok(
                Map.of("message", "Prescription updated successfully",
                        "data", prescriptionService.updatePrescription(prescriptionId, request)));
    }

    // ============================================
    // DOCTOR ONLY — Update status
    // ============================================
    @Operation(summary = "Update prescription status (DOCTOR only)")
    @PatchMapping("/{prescriptionId}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updatePrescriptionStatus(
            @PathVariable @Positive(message = "Prescription ID must be positive") Long prescriptionId,
            @RequestParam PrescriptionStatus status) {
        log.info("Updating prescription ID={} status to {}", prescriptionId, status);
        return ResponseEntity.ok(
                Map.of("message", "Prescription status updated successfully",
                        "data", prescriptionService.updatePrescriptionStatus(prescriptionId, status)));
    }

    // ============================================
    // DOCTOR ONLY — Delete prescription
    // ============================================
    @Operation(summary = "Delete prescription (DOCTOR only)")
    @DeleteMapping("/{prescriptionId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> deletePrescription(
            @PathVariable @Positive(message = "Prescription ID must be positive") Long prescriptionId) {
        log.info("Deleting prescription ID: {}", prescriptionId);
        prescriptionService.deletePrescription(prescriptionId);
        return ResponseEntity.ok(
                Map.of("message", "Prescription deleted successfully", "prescriptionId", prescriptionId));
    }

    // ============================================
    // DOCTOR ONLY — Email prescription to patient
    // ============================================
    @Operation(summary = "Email prescription (DOCTOR only)")
    @PostMapping("/{prescriptionId}/email")
    @PreAuthorize("hasRole('DOCTOR')")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> emailPrescription(
            @PathVariable @Positive(message = "Prescription ID must be positive") Long prescriptionId) {
        log.info("Emailing prescription ID: {}", prescriptionId);

        Prescription prescription = prescriptionRepository
                .findByPrescriptionIdAndIsActiveTrue(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        String patientEmail;
        try {
            Map<String, Object> response = patientServiceClient.getPatientById(prescription.getPatientId());
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            patientEmail = (String) data.get("patientEmail");
        } catch (Exception e) {
            log.error("Failed to fetch patient email: {}", e.getMessage());
            throw new ServiceUnavailableException("Could not fetch patient email from Patient Service");
        }

        if (patientEmail == null || patientEmail.isBlank()) {
            throw new BadRequestException("Patient does not have an email address on file");
        }

        emailService.sendPrescriptionEmail(prescription, patientEmail);

        return ResponseEntity.ok(
                Map.of("message", "Prescription emailed successfully to " + patientEmail,
                        "prescriptionId", prescriptionId));
    }

    // ============================================
    // DOCTOR + PATIENT — View prescriptions
    // ============================================
    @Operation(summary = "Get prescription by ID (DOCTOR and PATIENT)")
    @GetMapping("/{prescriptionId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<?> getPrescriptionById(
            @PathVariable @Positive(message = "Prescription ID must be positive") Long prescriptionId) {
        log.info("Fetching prescription by ID: {}", prescriptionId);
        return ResponseEntity.ok(
                Map.of("message", "Prescription retrieved successfully",
                        "data", prescriptionService.getPrescriptionById(prescriptionId)));
    }

    @Operation(summary = "Get all prescriptions (DOCTOR and PATIENT)")
    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<?> getAllPrescriptions() {
        log.info("Fetching all prescriptions");
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(
                Map.of("message", "Prescriptions fetched successfully",
                        "count", prescriptions.size(),
                        "data", prescriptions));
    }

    @Operation(summary = "Get all prescriptions paginated (DOCTOR and PATIENT)")
    @GetMapping("/page")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<?> getAllPrescriptionsPaginated(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "prescriptionId") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PrescriptionResponseDTO> prescriptions = prescriptionService.getAllPrescriptions(pageable);

        return ResponseEntity.ok(Map.of(
                "message", "Prescriptions fetched successfully",
                "data", prescriptions.getContent(),
                "currentPage", prescriptions.getNumber(),
                "totalItems", prescriptions.getTotalElements(),
                "totalPages", prescriptions.getTotalPages()));
    }

    @Operation(summary = "Get prescriptions by doctor (DOCTOR only)")
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getPrescriptionsByDoctorId(
            @PathVariable @Positive(message = "Doctor ID must be positive") Long doctorId) {
        log.info("Fetching prescriptions for doctor ID: {}", doctorId);
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(
                Map.of("message", "Prescriptions fetched successfully",
                        "count", prescriptions.size(),
                        "data", prescriptions));
    }

    @Operation(summary = "Get prescriptions by patient (DOCTOR and PATIENT)")
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<?> getPrescriptionsByPatientId(
            @PathVariable @Positive(message = "Patient ID must be positive") Long patientId) {
        log.info("Fetching prescriptions for patient ID: {}", patientId);
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(
                Map.of("message", "Prescriptions fetched successfully",
                        "count", prescriptions.size(),
                        "data", prescriptions));
    }

    @Operation(summary = "Get prescriptions by status (DOCTOR only)")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getPrescriptionsByStatus(@PathVariable PrescriptionStatus status) {
        log.info("Fetching prescriptions with status: {}", status);
        List<PrescriptionResponseDTO> prescriptions = prescriptionService.getPrescriptionsByStatus(status);
        return ResponseEntity.ok(
                Map.of("message", "Prescriptions fetched successfully",
                        "count", prescriptions.size(),
                        "data", prescriptions));
    }

    // ============================================
    // DOCTOR + PATIENT — Download PDF
    // ============================================
    @Operation(summary = "Download prescription PDF (DOCTOR and PATIENT)")
    @GetMapping("/{prescriptionId}/download")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(
            @PathVariable @Positive(message = "Prescription ID must be positive") Long prescriptionId) {
        log.info("Downloading PDF for prescription ID: {}", prescriptionId);

        Prescription prescription = prescriptionRepository
                .findByPrescriptionIdAndIsActiveTrue(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        byte[] pdfBytes = pdfService.generatePrescriptionPdf(prescription);
        String fileName = "Prescription_" + prescriptionId + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }
}
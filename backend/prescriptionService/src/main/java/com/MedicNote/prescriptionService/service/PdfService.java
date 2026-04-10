package com.MedicNote.prescriptionService.service;

import com.MedicNote.prescriptionService.entity.Medication;
import com.MedicNote.prescriptionService.entity.Prescription;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class PdfService {

    public byte[] generatePrescriptionPdf(Prescription prescription) {

        log.info("Generating PDF for prescription ID={}", prescription.getPrescriptionId());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 40, 40, 50, 50);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // ============ HEADER ============
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0, 102, 153));
            Paragraph title = new Paragraph("MedicNote", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font subtitleFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.GRAY);
            Paragraph subtitle = new Paragraph("Digital Prescription", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Horizontal line
            document.add(new Paragraph(" "));
            document.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator()));
            document.add(new Paragraph(" "));

            // ============ PRESCRIPTION INFO ============
            Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

            addField(document, "Prescription ID:", String.valueOf(prescription.getPrescriptionId()), labelFont, valueFont);
            addField(document, "Date:", prescription.getCreatedAt().toLocalDate().toString(), labelFont, valueFont);
            addField(document, "Status:", prescription.getStatus().name(), labelFont, valueFont);

            document.add(new Paragraph(" "));

            // ============ DOCTOR INFO ============
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(0, 102, 153));
            Paragraph doctorSection = new Paragraph("Doctor Information", sectionFont);
            doctorSection.setSpacingAfter(8);
            document.add(doctorSection);

            addField(document, "Doctor Name:", prescription.getDoctorName(), labelFont, valueFont);
            addField(document, "Doctor ID:", String.valueOf(prescription.getDoctorId()), labelFont, valueFont);

            document.add(new Paragraph(" "));

            // ============ PATIENT INFO ============
            Paragraph patientSection = new Paragraph("Patient Information", sectionFont);
            patientSection.setSpacingAfter(8);
            document.add(patientSection);

            addField(document, "Patient Name:", prescription.getPatientName(), labelFont, valueFont);
            addField(document, "Patient ID:", String.valueOf(prescription.getPatientId()), labelFont, valueFont);

            document.add(new Paragraph(" "));

            // ============ DIAGNOSIS ============
            Paragraph diagnosisSection = new Paragraph("Diagnosis", sectionFont);
            diagnosisSection.setSpacingAfter(8);
            document.add(diagnosisSection);

            Paragraph diagnosisText = new Paragraph(prescription.getDiagnosis(), valueFont);
            diagnosisText.setSpacingAfter(10);
            document.add(diagnosisText);

            if (prescription.getNotes() != null && !prescription.getNotes().isBlank()) {
                addField(document, "Notes:", prescription.getNotes(), labelFont, valueFont);
            }

            document.add(new Paragraph(" "));

            // ============ MEDICATIONS TABLE ============
            Paragraph medsSection = new Paragraph("Medications", sectionFont);
            medsSection.setSpacingAfter(10);
            document.add(medsSection);

            if (prescription.getMedications() != null && !prescription.getMedications().isEmpty()) {

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{3f, 2f, 2f, 2f, 3f});

                // Table headers
                Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
                Color headerBg = new Color(0, 102, 153);

                addTableHeader(table, "Medicine", headerFont, headerBg);
                addTableHeader(table, "Dosage", headerFont, headerBg);
                addTableHeader(table, "Frequency", headerFont, headerBg);
                addTableHeader(table, "Duration", headerFont, headerBg);
                addTableHeader(table, "Instructions", headerFont, headerBg);

                // Table rows
                Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
                for (Medication med : prescription.getMedications()) {
                    table.addCell(new PdfPCell(new Phrase(med.getMedicineName(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(med.getDosage(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(med.getFrequency(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(med.getDuration(), cellFont)));
                    table.addCell(new PdfPCell(new Phrase(
                            med.getInstructions() != null ? med.getInstructions() : "-", cellFont)));
                }

                document.add(table);
            }

            // ============ FOOTER ============
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator()));

            Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph(
                    "This is a digitally generated prescription by MedicNote. Generated on: " +
                            prescription.getCreatedAt().toString(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

        } catch (DocumentException e) {
            log.error("Error generating PDF for prescription ID={}: {}", prescription.getPrescriptionId(), e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        } finally {
            document.close();
        }

        log.info("PDF generated successfully for prescription ID={}", prescription.getPrescriptionId());
        return outputStream.toByteArray();
    }

    private void addField(Document document, String label, String value, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value, valueFont));
        p.setSpacingAfter(4);
        document.add(p);
    }

    private void addTableHeader(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}

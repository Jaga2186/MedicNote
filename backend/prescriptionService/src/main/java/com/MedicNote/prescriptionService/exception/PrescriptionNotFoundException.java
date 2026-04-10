package com.MedicNote.prescriptionService.exception;

public class PrescriptionNotFoundException extends RuntimeException {

    public PrescriptionNotFoundException(String message) {
        super(message);
    }

    public PrescriptionNotFoundException(Long prescriptionId) {
        super("Prescription not found with id: " + prescriptionId);
    }
}

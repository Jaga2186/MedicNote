package com.MedicNote.doctorService.exception;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(String message) {
        super(message);
    }

    public DoctorNotFoundException(Long doctorId) {
        super("Doctor not found with id: " + doctorId);
    }
}
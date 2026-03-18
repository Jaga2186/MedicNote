package com.MedicNote.doctorService.exception;

import java.io.Serial;

public class DoctorNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DoctorNotFoundException(String message) {
        super(message);
    }

    public DoctorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DoctorNotFoundException(Long doctorId) {
        super("Doctor with ID: " + doctorId + " not found.");
    }

}

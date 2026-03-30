package com.MedicNote.patientService.exception;

import java.io.Serial;

public class PatientAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PatientAlreadyExistsException(String message) {
        super(message);
    }

}

package com.MedicNote.doctorService.exception;

import java.io.Serial;

public class DoctorAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DoctorAlreadyExistsException(String message) {
        super(message);
    }

    public DoctorAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}

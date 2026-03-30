package com.MedicNote.patientService.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;        // error code
    private String message;      // user readable message
    private String path;
    private String method;
    private String traceId;
    private Map<String, String> validationErrors;
}
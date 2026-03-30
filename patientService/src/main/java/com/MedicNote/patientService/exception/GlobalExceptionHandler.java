package com.MedicNote.patientService.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 🔧 Central error builder
    private ErrorResponse buildError(
            HttpStatus status,
            String message,
            ErrorCode errorCode,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorCode.name())
                .message(message)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .traceId(UUID.randomUUID().toString().substring(0,12))
                .validationErrors(validationErrors)
                .build();
    }

    // ================= PATIENT NOT FOUND =================
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(
            PatientNotFoundException ex, HttpServletRequest request) {

        log.warn("Patient not found: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, ex.getMessage(),
                        ErrorCode.PATIENT_NOT_FOUND, request, null),
                HttpStatus.NOT_FOUND);
    }

    // ================= PATIENT EXISTS =================
    @ExceptionHandler(PatientAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePatientExists(
            PatientAlreadyExistsException ex, HttpServletRequest request) {

        log.warn("Patient exists: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.CONFLICT, ex.getMessage(),
                        ErrorCode.PATIENT_ALREADY_EXISTS, request, null),
                HttpStatus.CONFLICT);
    }

    // ================= INVALID CREDENTIALS =================
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {

        log.warn("Invalid credentials: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(),
                        ErrorCode.INVALID_CREDENTIALS, request, null),
                HttpStatus.UNAUTHORIZED);
    }

    // ================= BAD REQUEST =================
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {

        log.warn("Bad request: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, ex.getMessage(),
                        ErrorCode.BAD_REQUEST, request, null),
                HttpStatus.BAD_REQUEST);
    }

    // ================= VALIDATION =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, "Validation failed",
                        ErrorCode.VALIDATION_FAILED, request, errors),
                HttpStatus.BAD_REQUEST);
    }

    // ================= GLOBAL =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error", ex);

        return new ResponseEntity<>(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unexpected error occurred",
                        ErrorCode.INTERNAL_SERVER_ERROR, request, null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
package com.MedicNote.prescriptionService.exception;

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
                .traceId(UUID.randomUUID().toString().substring(0, 12))
                .validationErrors(validationErrors)
                .build();
    }

    @ExceptionHandler(PrescriptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePrescriptionNotFound(
            PrescriptionNotFoundException ex, HttpServletRequest request) {

        log.warn("Prescription not found: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, ex.getMessage(),
                        ErrorCode.PRESCRIPTION_NOT_FOUND, request, null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {

        log.warn("Invalid credentials: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(),
                        ErrorCode.INVALID_CREDENTIALS, request, null),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {

        log.warn("Bad request: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, ex.getMessage(),
                        ErrorCode.BAD_REQUEST, request, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(
            ServiceUnavailableException ex, HttpServletRequest request) {

        log.error("Service unavailable: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(),
                        ErrorCode.SERVICE_UNAVAILABLE, request, null),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

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

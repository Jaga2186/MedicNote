package com.MedicNote.authService.exception;

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

    // ================= COMMON BUILDER =================
    private ErrorResponse buildError(
            HttpStatus status,
            String message,
            String errorCode,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .traceId(UUID.randomUUID().toString().substring(0, 12))
                .validationErrors(validationErrors)
                .build();
    }

    // ================= VALIDATION =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, "Validation failed", "VALIDATION_FAILED", request, errors),
                HttpStatus.BAD_REQUEST);
    }

    // ================= DOWN STREAM =================
    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ErrorResponse> handleDownstream(
            DownstreamServiceException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = switch (status) {
            case UNAUTHORIZED        -> "Invalid email or password.";
            case NOT_FOUND           -> "Account not found. Please check your email or register.";
            case FORBIDDEN           -> "Access denied.";
            case REQUEST_TIMEOUT     -> "The request timed out. Please try again.";
            case SERVICE_UNAVAILABLE -> "Service is temporarily unavailable. Please try again later.";
            default                  -> ex.getMessage();
        };

        String errorCode = switch (status) {
            case UNAUTHORIZED        -> "INVALID_CREDENTIALS";
            case NOT_FOUND           -> "ACCOUNT_NOT_FOUND";
            case FORBIDDEN           -> "ACCESS_DENIED";
            case REQUEST_TIMEOUT     -> "REQUEST_TIMEOUT";
            case SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE";
            default                  -> "DOWNSTREAM_ERROR";
        };

        log.error("Downstream service error [{}]: {}", ex.getStatusCode(), ex.getMessage());
        return new ResponseEntity<>(
                buildError(status, message, errorCode, request, null),
                status);
    }

    // ================= GLOBAL =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex, HttpServletRequest request) {
        log.error("Auth error: {}", ex.getMessage());
        return new ResponseEntity<>(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "AUTH_ERROR", request, null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

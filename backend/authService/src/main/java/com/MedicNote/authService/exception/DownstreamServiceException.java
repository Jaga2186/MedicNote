package com.MedicNote.authService.exception;

import lombok.Getter;

@Getter
public class DownstreamServiceException extends RuntimeException {

    private final int statusCode;

    public DownstreamServiceException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
package com.MedicNote.authService.config;

import com.MedicNote.authService.exception.DownstreamServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String rawBody = "";

        try {
            if (response.body() != null) {
                InputStream inputStream = response.body().asInputStream();
                rawBody = new String(inputStream.readAllBytes());
            }
        } catch (Exception e) {
            log.error("Failed to read Feign error response body: {}", e.getMessage());
        }

        log.error("═══════════════════════════════════════════════════");
        log.error("FeignErrorDecoder triggered");
        log.error("Method      : {}", methodKey);
        log.error("HTTP Status : {}", status);
        log.error("Raw Body    : {}", rawBody.isEmpty() ? "<empty>" : rawBody);
        log.error("═══════════════════════════════════════════════════");

        // Try to extract message from JSON body
        String message = rawBody;
        try {
            if (!rawBody.isEmpty()) {
                JsonNode json = objectMapper.readTree(rawBody);
                if (json.has("message")) {
                    message = json.get("message").asText();
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse error body as JSON, using raw body as message");
        }

        if (message.isEmpty()) {
            message = "Downstream error with status: " + status;
        }

        log.error("Extracted message: {}", message);

        // Directly return DownstreamServiceException with real HTTP status
        return new DownstreamServiceException(status, message, null);
    }
}
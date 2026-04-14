package com.MedicNote.authService.feign;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class DoctorServiceClientFallback implements FallbackFactory<DoctorServiceClient> {

    @Override
    public DoctorServiceClient create(Throwable cause) {
        log.error("Doctor Service fallback triggered: {}", cause.getMessage(), cause);
        return new DoctorServiceClient() {

            @Override
            public Map<String, Object> registerDoctor(Map<String, Object> request) {
                throw buildException("Doctor Service", cause);
            }

            @Override
            public Map<String, Object> loginDoctor(Map<String, Object> request) {
                throw buildException("Doctor Service", cause);
            }

            @Override
            public Map<String, Object> checkDoctorEmail(String email) {
                throw buildException("Doctor Service", cause);
            }
        };
    }

    private RuntimeException buildException(String serviceName, Throwable cause) {
        if (cause instanceof FeignException fe) {
            // Propagate the actual HTTP error from the downstream service
            String body = fe.contentUTF8();
            log.error("{} returned HTTP {}: {}", serviceName, fe.status(), body);
            return new RuntimeException(body.isEmpty() ? fe.getMessage() : body, cause);
        }
        if (cause instanceof TimeoutException) {
            log.error("{} call timed out", serviceName);
            return new RuntimeException(serviceName + " call timed out. Please try again.", cause);
        }
        log.error("{} is unavailable: {}", serviceName, cause.getMessage());
        return new RuntimeException(serviceName + " is currently unavailable: " + cause.getMessage(), cause);
    }
}

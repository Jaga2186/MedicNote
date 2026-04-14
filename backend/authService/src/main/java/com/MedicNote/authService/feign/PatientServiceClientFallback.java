package com.MedicNote.authService.feign;

import com.MedicNote.authService.dto.PatientRegisterRequestDTO;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class PatientServiceClientFallback implements FallbackFactory<PatientServiceClient> {

    @Override
    public PatientServiceClient create(Throwable cause) {
        log.error("Patient Service fallback triggered: {}", cause.getMessage(), cause);
        return new PatientServiceClient() {

            @Override
            public Map<String, Object> registerPatient(PatientRegisterRequestDTO request) {
                throw buildException("Patient Service", cause);
            }

            @Override
            public Map<String, Object> loginPatient(Map<String, Object> request) {
                throw buildException("Patient Service", cause);
            }
        };
    }

    private RuntimeException buildException(String serviceName, Throwable cause) {
        if (cause instanceof FeignException fe) {
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

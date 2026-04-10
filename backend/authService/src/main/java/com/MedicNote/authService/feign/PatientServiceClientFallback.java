package com.MedicNote.authService.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PatientServiceClientFallback implements FallbackFactory<PatientServiceClient> {

    @Override
    public PatientServiceClient create(Throwable cause) {
        log.error("Patient Service fallback triggered: {}", cause.getMessage());
        return new PatientServiceClient() {

            @Override
            public Map<String, Object> registerPatient(Map<String, Object> request) {
                throw new RuntimeException("Patient Service is currently unavailable. Please try again later.");
            }

            @Override
            public Map<String, Object> loginPatient(Map<String, Object> request) {
                throw new RuntimeException("Patient Service is currently unavailable. Please try again later.");
            }
        };
    }
}

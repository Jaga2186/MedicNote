package com.MedicNote.authService.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DoctorServiceClientFallback implements FallbackFactory<DoctorServiceClient> {

    @Override
    public DoctorServiceClient create(Throwable cause) {
        log.error("Doctor Service fallback triggered: {}", cause.getMessage());
        return new DoctorServiceClient() {

            @Override
            public Map<String, Object> registerDoctor(Map<String, Object> request) {
                throw new RuntimeException("Doctor Service is currently unavailable. Please try again later.");
            }

            @Override
            public Map<String, Object> loginDoctor(Map<String, Object> request) {
                throw new RuntimeException("Doctor Service is currently unavailable. Please try again later.");
            }

            @Override
            public Map<String, Object> checkDoctorEmail(String email) {
                throw new RuntimeException("Doctor Service is currently unavailable. Please try again later.");
            }
        };
    }
}

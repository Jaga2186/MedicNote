package com.MedicNote.prescriptionService.feign;

import com.MedicNote.prescriptionService.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DoctorServiceClientFallback implements FallbackFactory<DoctorServiceClient> {

    @Override
    public DoctorServiceClient create(Throwable cause) {
        return new DoctorServiceClient() {
            @Override
            public Map<String, Object> getDoctorById(Long id) {
                log.error("Doctor Service unavailable. Fallback triggered for getDoctorById({}): {}", id, cause.getMessage());
                throw new ServiceUnavailableException("Doctor Service is currently unavailable. Please try again later.");
            }
        };
    }
}

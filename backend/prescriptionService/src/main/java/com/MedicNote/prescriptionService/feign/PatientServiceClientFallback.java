package com.MedicNote.prescriptionService.feign;

import com.MedicNote.prescriptionService.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PatientServiceClientFallback implements FallbackFactory<PatientServiceClient> {

    @Override
    public PatientServiceClient create(Throwable cause) {
        return new PatientServiceClient() {
            @Override
            public Map<String, Object> getPatientById(Long patientId) {
                log.error("Patient Service unavailable. Fallback triggered for getPatientById({}): {}", patientId, cause.getMessage());
                throw new ServiceUnavailableException("Patient Service is currently unavailable. Please try again later.");
            }
        };
    }
}

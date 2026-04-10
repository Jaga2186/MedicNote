package com.MedicNote.prescriptionService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "PATIENT-SERVICE", fallbackFactory = PatientServiceClientFallback.class)
public interface PatientServiceClient {

    @GetMapping("/api/patients/{patientId}")
    Map<String, Object> getPatientById(@PathVariable("patientId") Long patientId);
}

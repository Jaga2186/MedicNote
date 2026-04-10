package com.MedicNote.authService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "PATIENT-SERVICE", fallbackFactory = PatientServiceClientFallback.class)
public interface PatientServiceClient {

    @PostMapping("/api/patients/register")
    Map<String, Object> registerPatient(@RequestBody Map<String, Object> request);

    @PostMapping("/api/patients/login")
    Map<String, Object> loginPatient(@RequestBody Map<String, Object> request);
}

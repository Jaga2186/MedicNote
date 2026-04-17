package com.MedicNote.authService.feign;

import com.MedicNote.authService.dto.PatientRegisterRequestDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "PATIENT-SERVICE",
        configuration = com.MedicNote.authService.config.FeignConfig.class,
        fallbackFactory = PatientServiceClientFallback.class
)
public interface PatientServiceClient {

    @PostMapping("/api/patients/register")
    Map<String, Object> registerPatient(@RequestBody PatientRegisterRequestDTO request);

    @PostMapping("/api/patients/login")
    Map<String, Object> loginPatient(@RequestBody Map<String, Object> request);

    @GetMapping("/api/patients/by-email/{email}")
    Map<String, Object> getPatientByEmail(@PathVariable("email") String email);

    @GetMapping("/api/patients/by-phone/{phone}")
    Map<String, Object> getPatientByPhone(@PathVariable("phone") String phone);
}

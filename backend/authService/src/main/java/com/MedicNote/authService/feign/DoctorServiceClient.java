package com.MedicNote.authService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "DOCTOR-SERVICE", fallbackFactory = DoctorServiceClientFallback.class)
public interface DoctorServiceClient {

    @PostMapping("/api/doctors/register")
    Map<String, Object> registerDoctor(@RequestBody Map<String, Object> request);

    @PostMapping("/api/doctors/login")
    Map<String, Object> loginDoctor(@RequestBody Map<String, Object> request);

    @GetMapping("/api/doctors/check-email/{email}")
    Map<String, Object> checkDoctorEmail(@PathVariable("email") String email);
}

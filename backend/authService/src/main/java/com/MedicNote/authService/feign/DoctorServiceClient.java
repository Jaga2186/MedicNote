package com.MedicNote.authService.feign;

import com.MedicNote.authService.dto.DoctorRegisterRequestDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "DOCTOR-SERVICE",
        configuration = com.MedicNote.authService.config.FeignConfig.class,
        fallbackFactory = DoctorServiceClientFallback.class
)
public interface DoctorServiceClient {

    @PostMapping("/api/doctors/register")
    Map<String, Object> registerDoctor(@RequestBody DoctorRegisterRequestDTO request);

    @PostMapping("/api/doctors/login")
    Map<String, Object> loginDoctor(@RequestBody Map<String, Object> request);

    @GetMapping("/api/doctors/by-email/{email}")
    Map<String, Object> getDoctorByEmail(@PathVariable("email") String email);

    @GetMapping("/api/doctors/by-phone/{phone}")
    Map<String, Object> getDoctorByPhone(@PathVariable("phone") String phone);
}

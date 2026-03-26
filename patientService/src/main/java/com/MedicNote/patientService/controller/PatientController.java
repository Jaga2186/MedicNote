package com.MedicNote.patientService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.MedicNote.patientService.dto.*;
import com.MedicNote.patientService.service.PatientService;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    @PostMapping("/register")
    public PatientResponseDTO register(@RequestBody PatientRequestDTO request) {
        return service.registerPatient(request);
    }

    @GetMapping("/{id}")
    public PatientResponseDTO getById(@PathVariable Long id) {
        return service.getPatientById(id);
    }

    @GetMapping
    public List<PatientResponseDTO> getAll() {
        return service.getAllPatients();
    }

    @PutMapping("/{id}")
    public PatientResponseDTO update(@PathVariable Long id,
                                     @RequestBody PatientRequestDTO request) {
        return service.updatePatient(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePatient(id);
    }
}
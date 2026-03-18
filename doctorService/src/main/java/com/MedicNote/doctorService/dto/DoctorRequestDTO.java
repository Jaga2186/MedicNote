package com.MedicNote.doctorService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class DoctorRequestDTO {

    @NotBlank(message = "Doctor name is required")
    @Size(max = 100, message = "Doctor name must be less than 100 characters")
    private String doctorName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String doctorEmail;

    @NotBlank(message = "Password is required")
    private String doctorPassword;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String doctorPhone;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String licenseNumber;
    private Integer experienceYears;
    private String hospitalName;
}

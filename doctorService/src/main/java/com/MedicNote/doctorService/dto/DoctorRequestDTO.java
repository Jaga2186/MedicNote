package com.MedicNote.doctorService.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class DoctorRequestDTO {

    @NotBlank(message = "Doctor name is required")
    @Size(max = 100, message = "Doctor name must be less than 100 characters")
    private String doctorName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String doctorEmail;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$",
            message = "Password must be at least 6 characters and include letter, number, and special character"
    )
    private String doctorPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number should be valid")
    private String doctorPhone;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License number is required")
    @Pattern(regexp =  "^[A-Z0-9-]+$", message = "Invalid license number")
    private String licenseNumber;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience must be >= 0")
    @Max(value = 60, message = "Experience must be <= 60")
    private Integer experienceYears;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;
}

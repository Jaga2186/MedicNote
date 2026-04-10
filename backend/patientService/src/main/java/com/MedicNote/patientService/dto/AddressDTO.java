package com.MedicNote.patientService.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
public class AddressDTO {

    @NotBlank(message = "Street is required")
    @Size(max = 150, message = "Street must be less than 150 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must be less than 100 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must be less than 100 characters")
    private String country;

    @NotBlank(message = "PinCode is required")
    @Pattern(regexp = "\\d{6}", message = "PinCode must be 6 digits")
    private String pinCode;
}
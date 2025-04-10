package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientUpdateDTO {
    @NotBlank
    @Size(max = 100, message = "Name cannot exceeed 100 characters")
    private String firstName;

    @NotBlank
    @Size(max = 100, message = "Name cannot exceeed 100 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

}

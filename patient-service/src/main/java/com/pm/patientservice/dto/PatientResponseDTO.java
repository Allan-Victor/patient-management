package com.pm.patientservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PatientResponseDTO {
    private String id;
    private String fullName;
    private String email;
    private String address;
    private String dateOfBirth;
}

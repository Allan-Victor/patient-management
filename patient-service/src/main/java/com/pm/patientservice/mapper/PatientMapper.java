package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "fullName", expression = "java(patient.getFullName())")
    @Mapping(target = "id", source = "id", qualifiedByName = "UUIDToString")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "dateToString")
    PatientResponseDTO patientToPatientResponseDTO(Patient patient);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "stringToDate")
    @Mapping(target = "registerDate", source = "registeredDate", qualifiedByName = "stringToDate")
    Patient toModel(PatientRequestDTO patientRequestDTO);

    @Named("UUIDToString")
    default String UUIDToString(UUID uuid) {
        return uuid.toString();
    }

    @Named("dateToString")
    default String dateToString(LocalDate date) {
        return date.toString();
    }

    @Named("stringToDate")
    default LocalDate stringToDate(String date) {
        return LocalDate.parse(date);
    }
}

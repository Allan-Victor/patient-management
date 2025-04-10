package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.PatientUpdateDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.exception.RequestValidationException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(patientMapper::patientToPatientResponseDTO)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        Patient patient = patientMapper.toModel(patientRequestDTO);

        emailCheck(patientRequestDTO, patient);

        Patient savedPatient = patientRepository.save(patient);

        // interact with grpc client
        billingServiceGrpcClient.createBillingAccount(savedPatient.getId().toString(),
                savedPatient.getFullName(), savedPatient.getEmail());

        kafkaProducer.sendEvent(savedPatient);

        return patientMapper.patientToPatientResponseDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, @Valid PatientUpdateDTO patientUpdateDTO) throws RequestValidationException {
        boolean changed = false;
        Patient patient = patientRepository.findById(id)
                .orElseThrow(()-> new PatientNotFoundException("Patient not found with ID: "+ id));

        if (patientRepository.existsByEmailAndIdNot(patientUpdateDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("A patient of this email " +
                    "already exists " + patientUpdateDTO.getEmail());
        }

        patient.setFirstName(patientUpdateDTO.getFirstName());
        patient.setLastName(patientUpdateDTO.getLastName());
        patient.setEmail(patientUpdateDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientUpdateDTO.getDateOfBirth()));
        changed = true;


        patientRepository.save(patient);

        if (!changed){
            throw new RequestValidationException("no data changes found");
        }
        return patientMapper.patientToPatientResponseDTO(patient);
    }

    private void emailCheck(PatientRequestDTO patientRequestDTO, Patient patient) {
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new EmailAlreadyExistsException("A patient of this email " +
                    "already exists " + patientRequestDTO.getEmail());
        }
    }

    public void delete(UUID id) {
        patientRepository.deleteById(id);
    }


}

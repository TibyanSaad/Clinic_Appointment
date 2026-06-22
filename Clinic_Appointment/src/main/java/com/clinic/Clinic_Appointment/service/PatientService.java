package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.Patient;
import com.clinic.Clinic_Appointment.model.PatientPhone;
import com.clinic.Clinic_Appointment.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Dto.PatientResponse createPatient(Dto.CreatePatientRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setBirthDate(request.getBirthDate());

        if (request.getPhones() != null) {
            List<PatientPhone> phones = new ArrayList<>();
            for (String number : request.getPhones()) {
                PatientPhone phone = new PatientPhone();
                phone.setPhoneNumber(number);
                phone.setPatient(patient);
                phones.add(phone);
            }
            patient.setPhones(phones);
        }

        Patient saved = patientRepository.save(patient);
        return toResponse(saved);
    }

    public Dto.PatientResponse getPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
        return toResponse(patient);
    }

    public Patient findById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
    }

    private Dto.PatientResponse toResponse(Patient patient) {
        List<String> phones = patient.getPhones() == null ? List.of() :
                patient.getPhones().stream().map(PatientPhone::getPhoneNumber).collect(Collectors.toList());
        return new Dto.PatientResponse(
                patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getBirthDate(), phones
        );
    }
}

package com.clinic.Clinic_Appointment.controller;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<Dto.PatientResponse> createPatient(@Valid @RequestBody Dto.CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Dto.PatientResponse> getPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatient(patientId));
    }
}

package com.clinic.Clinic_Appointment.controller;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.service.VisitRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class VisitRecordController {

    private final VisitRecordService visitRecordService;

    public VisitRecordController(VisitRecordService visitRecordService) {
        this.visitRecordService = visitRecordService;
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<Dto.VisitRecordResponse>> getPatientHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(visitRecordService.getPatientHistory(patientId));
    }
}

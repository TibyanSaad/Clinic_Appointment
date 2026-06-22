package com.clinic.Clinic_Appointment.controller;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.service.AppointmentService;
import com.clinic.Clinic_Appointment.service.VisitRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final VisitRecordService visitRecordService;

    public AppointmentController(AppointmentService appointmentService, VisitRecordService visitRecordService) {
        this.appointmentService = appointmentService;
        this.visitRecordService = visitRecordService;
    }

    @PostMapping
    public ResponseEntity<Dto.AppointmentResponse> bookAppointment(
            @Valid @RequestBody Dto.BookAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.bookAppointment(request));
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<Dto.AppointmentResponse> cancelAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/reschedule")
    public ResponseEntity<Dto.AppointmentResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody Dto.RescheduleRequest request) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(appointmentId, request));
    }

    @PostMapping("/{appointmentId}/complete")
    public ResponseEntity<Dto.AppointmentResponse> completeAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/visit")
    public ResponseEntity<Dto.VisitRecordResponse> recordVisit(
            @PathVariable Long appointmentId,
            @Valid @RequestBody Dto.RecordVisitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitRecordService.recordVisit(appointmentId, request));
    }
}

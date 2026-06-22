package com.clinic.Clinic_Appointment.controller;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.service.DoctorService;
import com.clinic.Clinic_Appointment.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final SlotService slotService;
    private final com.clinic.Clinic_Appointment.service.AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, SlotService slotService,
                            com.clinic.Clinic_Appointment.service.AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.slotService = slotService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Dto.DoctorResponse> createDoctor(@Valid @RequestBody Dto.CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(request));
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<Dto.DoctorResponse> getDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctor(doctorId));
    }

    @PostMapping("/{doctorId}/slots")
    public ResponseEntity<List<Dto.SlotResponse>> generateSlots(
            @PathVariable Long doctorId,
            @Valid @RequestBody Dto.GenerateSlotsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(slotService.generateSlots(doctorId, request));
    }

    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<Dto.SlotResponse>> getSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotService.getSlotsForDoctor(doctorId, date));
    }

    @GetMapping("/{doctorId}/schedule")
    public ResponseEntity<List<Dto.ScheduleSlotResponse>> getSchedule(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getDoctorSchedule(doctorId, date));
    }
}

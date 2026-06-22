package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.AppointmentSlot;
import com.clinic.Clinic_Appointment.model.Doctor;
import com.clinic.Clinic_Appointment.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotService {

    private final AppointmentSlotRepository slotRepository;
    private final DoctorService doctorService;

    public SlotService(AppointmentSlotRepository slotRepository, DoctorService doctorService) {
        this.slotRepository = slotRepository;
        this.doctorService = doctorService;
    }

    @Transactional
    public List<Dto.SlotResponse> generateSlots(Long doctorId, Dto.GenerateSlotsRequest request) {
        Doctor doctor = doctorService.findById(doctorId);
        LocalDate date = request.getDate();
        int durationMinutes = request.getSlotDurationMinutes();

        List<AppointmentSlot> created = new ArrayList<>();
        LocalTime current = doctor.getWorkingHoursStart();

        while (current.plusMinutes(durationMinutes).compareTo(doctor.getWorkingHoursEnd()) <= 0) {
            LocalTime slotEnd = current.plusMinutes(durationMinutes);

            // Skip if slot already exists for this doctor/date/time
            if (!slotRepository.existsByDoctorIdAndSlotDateAndStartTime(doctorId, date, current)) {
                AppointmentSlot slot = new AppointmentSlot();
                slot.setDoctor(doctor);
                slot.setSlotDate(date);
                slot.setStartTime(current);
                slot.setEndTime(slotEnd);
                slot.setAvailable(true);
                created.add(slotRepository.save(slot));
            }

            current = current.plusMinutes(durationMinutes);
        }

        if (created.isEmpty()) {
            throw new ConflictException("All slots for this date already exist or no time fits the working hours");
        }

        return created.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<Dto.SlotResponse> getSlotsForDoctor(Long doctorId, LocalDate date) {
        doctorService.findById(doctorId); // validate doctor exists
        return slotRepository.findByDoctorIdAndSlotDate(doctorId, date)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AppointmentSlot findById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Slot not found with ID: " + slotId));
    }

    public void markUnavailable(AppointmentSlot slot) {
        slot.setAvailable(false);
        slotRepository.save(slot);
    }

    public void markAvailable(AppointmentSlot slot) {
        slot.setAvailable(true);
        slotRepository.save(slot);
    }

    private Dto.SlotResponse toResponse(AppointmentSlot slot) {
        return new Dto.SlotResponse(
                slot.getId(), slot.getSlotDate(), slot.getStartTime(),
                slot.getEndTime(), slot.isAvailable()
        );
    }
}

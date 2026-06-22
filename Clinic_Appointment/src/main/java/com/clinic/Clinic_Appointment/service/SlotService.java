package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.AppointmentSlot;
import com.clinic.Clinic_Appointment.model.Doctor;
import com.clinic.Clinic_Appointment.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SlotService {

    private static final int SLOT_DURATION_MINUTES = 30;

    private final AppointmentSlotRepository slotRepository;
    private final DoctorService doctorService;

    public SlotService(AppointmentSlotRepository slotRepository, DoctorService doctorService) {
        this.slotRepository = slotRepository;
        this.doctorService = doctorService;
    }

    @Transactional
    public AppointmentSlot createSlot(Doctor doctor, LocalDate date, LocalTime startTime) {
        LocalTime endTime = startTime.plusMinutes(SLOT_DURATION_MINUTES);

        // Must fit within doctor's working hours
        if (startTime.isBefore(doctor.getWorkingHoursStart()) || endTime.isAfter(doctor.getWorkingHoursEnd())) {
            throw new BadRequestException(
                    "Requested time " + startTime + " is outside the doctor's working hours ("
                            + doctor.getWorkingHoursStart() + " - " + doctor.getWorkingHoursEnd() + ")"
            );
        }

        // All conflict checks (doctor overlap, patient overlap) are handled
        // in AppointmentService before this method is called.
        // This method only creates and saves the slot.
        AppointmentSlot slot = new AppointmentSlot();
        slot.setDoctor(doctor);
        slot.setSlotDate(date);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setAvailable(false);

        return slotRepository.save(slot);
    }

    public List<AppointmentSlot> getSlotsForDoctorOnDate(Long doctorId, LocalDate date) {
        return slotRepository.findByDoctorIdAndSlotDate(doctorId, date);
    }

    public AppointmentSlot findById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Slot not found with ID: " + slotId));
    }

    public void markAvailable(AppointmentSlot slot) {
        slot.setAvailable(true);
        slotRepository.save(slot);
    }

    public void markUnavailable(AppointmentSlot slot) {
        slot.setAvailable(false);
        slotRepository.save(slot);
    }
}
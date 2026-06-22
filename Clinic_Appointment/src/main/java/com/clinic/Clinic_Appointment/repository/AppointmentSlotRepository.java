package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    List<AppointmentSlot> findByDoctorIdAndSlotDate(Long doctorId, LocalDate slotDate);

    // Used to prevent double-booking: checks if a slot exists at that time that is already taken
    boolean existsByDoctorIdAndSlotDateAndStartTimeAndIsAvailableFalse(
            Long doctorId, LocalDate slotDate, LocalTime startTime);
}


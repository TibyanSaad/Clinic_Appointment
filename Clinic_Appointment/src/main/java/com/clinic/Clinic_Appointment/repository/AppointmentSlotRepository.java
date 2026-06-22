package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    List<AppointmentSlot> findByDoctorIdAndSlotDate(Long doctorId, LocalDate slotDate);

    boolean existsByDoctorIdAndSlotDateAndStartTime(Long doctorId, LocalDate slotDate, java.time.LocalTime startTime);
}

package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    List<AppointmentSlot> findByDoctorIdAndSlotDate(Long doctorId, LocalDate slotDate);

    // Checks if doctor has a booked (unavailable) slot that overlaps the requested time range
    @Query("""
        SELECT COUNT(s) > 0 FROM AppointmentSlot s
        WHERE s.doctor.id = :doctorId
        AND s.slotDate = :date
        AND s.isAvailable = false
        AND s.startTime < :endTime
        AND s.endTime > :startTime
    """)
    boolean existsDoctorOverlappingSlot(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Checks if patient has any booked (unavailable) slot with any doctor that overlaps the requested time range
    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.patient.id = :patientId
        AND a.slot.slotDate = :date
        AND a.status = 'SCHEDULED'
        AND a.slot.startTime < :endTime
        AND a.slot.endTime > :startTime
    """)
    boolean existsPatientOverlappingAppointment(
            @Param("patientId") Long patientId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}

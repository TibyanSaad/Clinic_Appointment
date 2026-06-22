package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Check if a patient already has an active (SCHEDULED) appointment with the same doctor on the same day
    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.patient.id = :patientId
        AND a.doctor.id = :doctorId
        AND a.slot.slotDate = :date
        AND a.status = 'SCHEDULED'
    """)
    boolean existsActiveAppointmentForPatientAndDoctorOnDate(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );

    // Find the active (SCHEDULED) appointment for a given slot — used for schedule view
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.slot.id = :slotId
        AND a.status = 'SCHEDULED'
    """)
    Optional<Appointment> findScheduledBySlotId(@Param("slotId") Long slotId);

    // Full visit history for a patient (completed appointments only)
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.patient.id = :patientId
        AND a.status = 'COMPLETED'
        ORDER BY a.slot.slotDate DESC
    """)
    List<Appointment> findCompletedByPatientId(@Param("patientId") Long patientId);
}
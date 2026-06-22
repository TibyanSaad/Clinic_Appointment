package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Check if patient already has an active appointment with same doctor on same day
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

    // Get all past completed visits for a patient
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.patient.id = :patientId
        AND a.status = 'COMPLETED'
        ORDER BY a.slot.slotDate DESC
    """)
    List<Appointment> findCompletedByPatientId(@Param("patientId") Long patientId);
}

package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    // Does a visit record exist for this appointment
    boolean existsByAppointmentId(Long appointmentId);

    //get visit records order by desc (newest at the top) for a patient
    List<VisitRecord> findByAppointmentPatientIdOrderByRecordedAtDesc(Long patientId);

    //get the visit record for a specific appointment
    Optional<VisitRecord> findByAppointmentId(Long appointmentId);
}

package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    List<VisitRecord> findByAppointmentPatientIdOrderByRecordedAtDesc(Long patientId);

    Optional<VisitRecord> findByAppointmentId(Long appointmentId);
}

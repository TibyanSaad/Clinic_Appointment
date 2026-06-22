package com.clinic.Clinic_Appointment.repository;

import com.clinic.Clinic_Appointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}

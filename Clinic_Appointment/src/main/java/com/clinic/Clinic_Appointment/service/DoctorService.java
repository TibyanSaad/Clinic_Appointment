package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.Doctor;
import com.clinic.Clinic_Appointment.repository.DoctorRepository;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Dto.DoctorResponse createDoctor(Dto.CreateDoctorRequest request) {
        if (!request.getWorkingHoursStart().isBefore(request.getWorkingHoursEnd())) {
            throw new BadRequestException("Working hours start must be before working hours end");
        }

        Doctor doctor = new Doctor();
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpeciality(request.getSpeciality());
        doctor.setWorkingHoursStart(request.getWorkingHoursStart());
        doctor.setWorkingHoursEnd(request.getWorkingHoursEnd());

        Doctor saved = doctorRepository.save(doctor);
        return toResponse(saved);
    }

    public Dto.DoctorResponse getDoctor(Long doctorId) {
        Doctor doctor = findById(doctorId);
        return toResponse(doctor);
    }

    public Doctor findById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + doctorId));
    }

    private Dto.DoctorResponse toResponse(Doctor doctor) {
        return new Dto.DoctorResponse(
                doctor.getId(), doctor.getFirstName(), doctor.getLastName(),
                doctor.getSpeciality(), doctor.getWorkingHoursStart(), doctor.getWorkingHoursEnd()
        );
    }
}

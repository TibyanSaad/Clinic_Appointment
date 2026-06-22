package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.*;
import com.clinic.Clinic_Appointment.repository.AppointmentRepository;
import com.clinic.Clinic_Appointment.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository slotRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final SlotService slotService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentSlotRepository slotRepository,
                              PatientService patientService,
                              DoctorService doctorService,
                              SlotService slotService) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.slotService = slotService;
    }

    @Transactional
    public Dto.AppointmentResponse bookAppointment(Dto.BookAppointmentRequest request) {
        Patient patient = patientService.findById(request.getPatientId());
        Doctor doctor = doctorService.findById(request.getDoctorId());
        AppointmentSlot slot = slotService.findById(request.getSlotId());

        // Validate slot belongs to the same doctor
        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("Slot does not belong to the specified doctor");
        }

        // Validate slot is available
        if (!slot.isAvailable()) {
            throw new ConflictException("This slot is already booked");
        }

        // Validate slot is not in the past
        if (slot.getSlotDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book a slot in the past");
        }

        // Validate patient doesn't already have an active appointment with this doctor on the same day
        boolean alreadyBooked = appointmentRepository.existsActiveAppointmentForPatientAndDoctorOnDate(
                patient.getId(), doctor.getId(), slot.getSlotDate()
        );
        if (alreadyBooked) {
            throw new ConflictException("Patient already has an active appointment with this doctor on " + slot.getSlotDate());
        }

        // Create the appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        // Mark slot as unavailable
        slotService.markUnavailable(slot);

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Transactional
    public Dto.AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED appointments can be cancelled. Current status: " + appointment.getStatus());
        }

        appointment.setStatus(Appointment.Status.CANCELLED);

        // Free up the slot
        slotService.markAvailable(appointment.getSlot());

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Transactional
    public Dto.AppointmentResponse rescheduleAppointment(Long appointmentId, Dto.RescheduleRequest request) {
        Appointment oldAppointment = findById(appointmentId);

        if (oldAppointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED appointments can be rescheduled. Current status: " + oldAppointment.getStatus());
        }

        AppointmentSlot newSlot = slotService.findById(request.getNewSlotId());

        // Validate new slot belongs to same doctor
        if (!newSlot.getDoctor().getId().equals(oldAppointment.getDoctor().getId())) {
            throw new BadRequestException("New slot must belong to the same doctor");
        }

        // Validate new slot is available
        if (!newSlot.isAvailable()) {
            throw new ConflictException("The requested new slot is not available");
        }

        // Validate new slot is not in the past
        if (newSlot.getSlotDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot reschedule to a slot in the past");
        }

        // Validate patient doesn't have another active appointment with same doctor on new slot's date
        // (excluding the current appointment being rescheduled)
        boolean alreadyBooked = appointmentRepository.existsActiveAppointmentForPatientAndDoctorOnDate(
                oldAppointment.getPatient().getId(), oldAppointment.getDoctor().getId(), newSlot.getSlotDate()
        );
        // Only block if the conflict is NOT the same day as the old slot (since we're moving away)
        if (alreadyBooked && !newSlot.getSlotDate().equals(oldAppointment.getSlot().getSlotDate())) {
            throw new ConflictException("Patient already has an active appointment with this doctor on " + newSlot.getSlotDate());
        }

        // Mark old appointment as RESCHEDULED and free its slot
        oldAppointment.setStatus(Appointment.Status.RESCHEDULED);
        slotService.markAvailable(oldAppointment.getSlot());

        // Create new appointment
        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(oldAppointment.getPatient());
        newAppointment.setDoctor(oldAppointment.getDoctor());
        newAppointment.setSlot(newSlot);
        newAppointment.setStatus(Appointment.Status.SCHEDULED);

        // Mark new slot as unavailable
        slotService.markUnavailable(newSlot);

        Appointment savedNew = appointmentRepository.save(newAppointment);

        // Link old appointment to the new one for history
        oldAppointment.setRescheduledTo(savedNew);
        appointmentRepository.save(oldAppointment);

        return toResponse(savedNew);
    }

    @Transactional
    public Dto.AppointmentResponse completeAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED appointments can be marked completed. Current status: " + appointment.getStatus());
        }

        appointment.setStatus(Appointment.Status.COMPLETED);
        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    public List<Dto.ScheduleSlotResponse> getDoctorSchedule(Long doctorId, LocalDate date) {
        doctorService.findById(doctorId); // validate doctor exists
        List<AppointmentSlot> slots = slotRepository.findByDoctorIdAndSlotDate(doctorId, date);

        return slots.stream().map(slot -> {
            // Find active appointment for this slot (if any)
            Appointment active = appointmentRepository.findAll().stream()
                    .filter(a -> a.getSlot().getId().equals(slot.getId())
                            && a.getStatus() == Appointment.Status.SCHEDULED)
                    .findFirst().orElse(null);

            String patientName = null;
            Long appointmentId = null;
            if (active != null) {
                patientName = active.getPatient().getFirstName() + " " + active.getPatient().getLastName();
                appointmentId = active.getId();
            }

            return new Dto.ScheduleSlotResponse(
                    slot.getId(), slot.getStartTime(), slot.getEndTime(),
                    slot.isAvailable(), appointmentId, patientName
            );
        }).collect(Collectors.toList());
    }

    public Appointment findById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));
    }

    private Dto.AppointmentResponse toResponse(Appointment a) {
        Long rescheduledToId = a.getRescheduledTo() != null ? a.getRescheduledTo().getId() : null;
        return new Dto.AppointmentResponse(
                a.getId(),
                a.getStatus().name(),
                a.getPatient().getId(),
                a.getPatient().getFirstName() + " " + a.getPatient().getLastName(),
                a.getDoctor().getId(),
                a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName(),
                a.getSlot().getSlotDate(),
                a.getSlot().getStartTime(),
                a.getSlot().getEndTime(),
                rescheduledToId
        );
    }
}

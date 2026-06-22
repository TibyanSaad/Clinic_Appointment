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

        // Check patient doesn't already have an active appointment with this doctor on this day
        boolean alreadyBooked = appointmentRepository.existsActiveAppointmentForPatientAndDoctorOnDate(
                patient.getId(), doctor.getId(), request.getDate()
        );
        if (alreadyBooked) {
            throw new ConflictException(
                    "Patient already has an active appointment with this doctor on " + request.getDate()
            );
        }

        // Create the slot on the spot (validates working hours + doctor double-booking inside)
        AppointmentSlot slot = slotService.createSlot(doctor, request.getDate(), request.getStartTime());

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public Dto.AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException(
                    "Only SCHEDULED appointments can be cancelled. Current status: " + appointment.getStatus()
            );
        }

        appointment.setStatus(Appointment.Status.CANCELLED);

        // Free the slot so it can be reused if someone else books that time
        slotService.markAvailable(appointment.getSlot());

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public Dto.AppointmentResponse rescheduleAppointment(Long appointmentId, Dto.RescheduleRequest request) {
        Appointment oldAppointment = findById(appointmentId);

        if (oldAppointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException(
                    "Only SCHEDULED appointments can be rescheduled. Current status: " + oldAppointment.getStatus()
            );
        }

        Doctor doctor = oldAppointment.getDoctor();

        // Check patient doesn't already have an active appointment with same doctor on the new day
        // (only matters if they're moving to a different date)
        boolean movingToNewDate = !request.getNewDate().equals(oldAppointment.getSlot().getSlotDate());
        if (movingToNewDate) {
            boolean conflict = appointmentRepository.existsActiveAppointmentForPatientAndDoctorOnDate(
                    oldAppointment.getPatient().getId(), doctor.getId(), request.getNewDate()
            );
            if (conflict) {
                throw new ConflictException(
                        "Patient already has an active appointment with this doctor on " + request.getNewDate()
                );
            }
        }

        // Mark old appointment as RESCHEDULED and free its slot
        oldAppointment.setStatus(Appointment.Status.RESCHEDULED);
        slotService.markAvailable(oldAppointment.getSlot());

        // Create a new slot on the spot for the new time (validates working hours + double-booking)
        AppointmentSlot newSlot = slotService.createSlot(doctor, request.getNewDate(), request.getNewStartTime());

        // Create the new appointment
        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(oldAppointment.getPatient());
        newAppointment.setDoctor(doctor);
        newAppointment.setSlot(newSlot);
        newAppointment.setStatus(Appointment.Status.SCHEDULED);

        Appointment savedNew = appointmentRepository.save(newAppointment);

        // Link old → new for history tracking
        oldAppointment.setRescheduledTo(savedNew);
        appointmentRepository.save(oldAppointment);

        return toResponse(savedNew);
    }

    @Transactional
    public Dto.AppointmentResponse completeAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException(
                    "Only SCHEDULED appointments can be marked as completed. Current status: " + appointment.getStatus()
            );
        }

        appointment.setStatus(Appointment.Status.COMPLETED);
        return toResponse(appointmentRepository.save(appointment));
    }

    public List<Dto.ScheduleSlotResponse> getDoctorSchedule(Long doctorId, LocalDate date) {
        doctorService.findById(doctorId); // validate doctor exists

        List<AppointmentSlot> slots = slotService.getSlotsForDoctorOnDate(doctorId, date);

        return slots.stream().map(slot -> {
            Appointment active = appointmentRepository.findScheduledBySlotId(slot.getId()).orElse(null);

            String patientName = null;
            Long apptId = null;
            if (active != null) {
                patientName = active.getPatient().getFirstName() + " " + active.getPatient().getLastName();
                apptId = active.getId();
            }

            return new Dto.ScheduleSlotResponse(
                    slot.getId(), slot.getStartTime(), slot.getEndTime(),
                    slot.isAvailable(), apptId, patientName
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
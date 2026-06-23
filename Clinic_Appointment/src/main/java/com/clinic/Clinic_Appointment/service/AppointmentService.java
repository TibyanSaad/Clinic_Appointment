package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.*;
import com.clinic.Clinic_Appointment.repository.AppointmentRepository;
import com.clinic.Clinic_Appointment.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    // to set the end time as 30mins after start time
    private static final int SLOT_DURATION_MINUTES = 30;

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
        //passing request check
        Patient patient = patientService.findById(request.getPatientId());
        Doctor doctor = doctorService.findById(request.getDoctorId());

        // calculate time slot
        LocalDate date = request.getDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusMinutes(SLOT_DURATION_MINUTES);

        // Check patient doesnt already have an active appointment with this doctor on this day
        boolean alreadyBookedSameDoctor = appointmentRepository.existsActiveAppointmentForPatientAndDoctorOnDate(
                patient.getId(), doctor.getId(), date
        );
        if (alreadyBookedSameDoctor) {
            throw new ConflictException(
                    "Patient already has an active appointment with this doctor on " + date
            );
        }

        // Check doctor is not already busy in this time range
        boolean doctorBusy = slotRepository.existsDoctorOverlappingSlot(
                doctor.getId(), date, startTime, endTime
        );
        if (doctorBusy) {
            throw new ConflictException(
                    "Doctor is already booked between " + startTime + " and " + endTime + " on " + date
            );
        }

        // Check patient is not already busy with another doctor in this time range
        boolean patientBusy = slotRepository.existsPatientOverlappingAppointment(
                patient.getId(), date, startTime, endTime
        );
        if (patientBusy) {
            throw new ConflictException(
                    "Patient already has an appointment with another doctor between " + startTime + " and " + endTime + " on " + date
            );
        }

        // All checks passed, create slot and book appointment
        AppointmentSlot slot = slotService.createSlot(doctor, date, startTime);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        return toResponse(appointmentRepository.save(appointment));
    }

    // cancelling an appointment
    @Transactional
    public Dto.AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException(
                    "Only SCHEDULED appointments can be cancelled. Current status: " + appointment.getStatus()
            );
        }

        appointment.setStatus(Appointment.Status.CANCELLED);
        slotService.markAvailable(appointment.getSlot());

        return toResponse(appointmentRepository.save(appointment));
    }

    //change appointment
    @Transactional
    public Dto.AppointmentResponse rescheduleAppointment(Long appointmentId, Dto.RescheduleRequest request) {
        Appointment oldAppointment = findById(appointmentId);

        if (oldAppointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new BadRequestException(
                    "Only SCHEDULED appointments can be rescheduled. Current status: " + oldAppointment.getStatus()
            );
        }

        Doctor doctor = oldAppointment.getDoctor();
        Patient patient = oldAppointment.getPatient();

        LocalDate newDate = request.getNewDate();
        LocalTime newStartTime = request.getNewStartTime();
        LocalTime newEndTime = newStartTime.plusMinutes(SLOT_DURATION_MINUTES);

        // Check patient doesn't already have an active appointment with same doctor on the new day
        boolean movingToNewDate = !newDate.equals(oldAppointment.getSlot().getSlotDate());
        if (movingToNewDate) {
            boolean conflict = appointmentRepository.existsActiveAppointmentForPatientAndDoctorOnDate(
                    patient.getId(), doctor.getId(), newDate
            );
            if (conflict) {
                throw new ConflictException(
                        "Patient already has an active appointment with this doctor on " + newDate
                );
            }
        }

        // Check doctor is not already busy in the new time range
        boolean doctorBusy = slotRepository.existsDoctorOverlappingSlot(
                doctor.getId(), newDate, newStartTime, newEndTime
        );
        if (doctorBusy) {
            throw new ConflictException(
                    "Doctor is already booked between " + newStartTime + " and " + newEndTime + " on " + newDate
            );
        }

        // Check patient is not already busy with another doctor in the new time range
        boolean patientBusy = slotRepository.existsPatientOverlappingAppointment(
                patient.getId(), newDate, newStartTime, newEndTime
        );
        if (patientBusy) {
            throw new ConflictException(
                    "Patient already has an appointment with another doctor between " + newStartTime + " and " + newEndTime + " on " + newDate
            );
        }

        // Mark old appointment as RESCHEDULED and free its slot
        oldAppointment.setStatus(Appointment.Status.RESCHEDULED);
        slotService.markAvailable(oldAppointment.getSlot());

        // Create new slot and appointment
        AppointmentSlot newSlot = slotService.createSlot(doctor, newDate, newStartTime);

        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(patient);
        newAppointment.setDoctor(doctor);
        newAppointment.setSlot(newSlot);
        newAppointment.setStatus(Appointment.Status.SCHEDULED);

        Appointment savedNew = appointmentRepository.save(newAppointment);

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

    //Get a doctors full schedule for a specific date, with patient info for each booked slot
    public List<Dto.ScheduleSlotResponse> getDoctorSchedule(Long doctorId, LocalDate date) {
        doctorService.findById(doctorId);

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

    // convert an Appointment entity into a AppointmentResponse dto to send to the client
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
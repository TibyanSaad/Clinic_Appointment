package com.clinic.Clinic_Appointment.service;

import com.clinic.Clinic_Appointment.dto.Dto;
import com.clinic.Clinic_Appointment.exception.GlobalExceptionHandler.*;
import com.clinic.Clinic_Appointment.model.Appointment;
import com.clinic.Clinic_Appointment.model.VisitRecord;
import com.clinic.Clinic_Appointment.repository.AppointmentRepository;
import com.clinic.Clinic_Appointment.repository.VisitRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VisitRecordService {

    private final VisitRecordRepository visitRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public VisitRecordService(VisitRecordRepository visitRecordRepository,
                              AppointmentRepository appointmentRepository,
                              AppointmentService appointmentService,
                              PatientService patientService) {
        this.visitRecordRepository = visitRecordRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @Transactional
    public Dto.VisitRecordResponse recordVisit(Long appointmentId, Dto.RecordVisitRequest request) {
        Appointment appointment = appointmentService.findById(appointmentId);

        if (appointment.getStatus() != Appointment.Status.COMPLETED) {
            throw new BadRequestException("Visit can only be recorded for COMPLETED appointments. Current status: " + appointment.getStatus());
        }

        if (visitRecordRepository.existsByAppointmentId(appointmentId)) {
            throw new ConflictException("A visit record already exists for appointment ID: " + appointmentId);
        }

        VisitRecord record = new VisitRecord();
        record.setAppointment(appointment);
        record.setDiagnosis(request.getDiagnosis());
        record.setPrescription(request.getPrescription());
        record.setRecordedAt(LocalDateTime.now());

        VisitRecord saved = visitRecordRepository.save(record);
        return toVisitResponse(saved);
    }

    // Returns full patient history — all appointments with visit details where available
    public List<Dto.PatientHistoryResponse> getPatientHistory(Long patientId) {
        patientService.findById(patientId); // validate patient exists

        List<Appointment> appointments = appointmentRepository.findAllByPatientId(patientId);

        return appointments.stream().map(appointment -> {
            Optional<VisitRecord> visitRecord = visitRecordRepository.findByAppointmentId(appointment.getId());

            Long visitId = null;
            String diagnosis = null;
            String prescription = null;
            LocalDateTime recordedAt = null;

            if (visitRecord.isPresent()) {
                visitId = visitRecord.get().getId();
                diagnosis = visitRecord.get().getDiagnosis();
                prescription = visitRecord.get().getPrescription();
                recordedAt = visitRecord.get().getRecordedAt();
            }

            Long rescheduledToId = appointment.getRescheduledTo() != null
                    ? appointment.getRescheduledTo().getId()
                    : null;

            return new Dto.PatientHistoryResponse(
                    appointment.getId(),
                    appointment.getStatus().name(),
                    appointment.getSlot().getSlotDate(),
                    appointment.getSlot().getStartTime(),
                    appointment.getSlot().getEndTime(),
                    appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
                    appointment.getDoctor().getSpeciality(),
                    rescheduledToId,
                    visitId,
                    diagnosis,
                    prescription,
                    recordedAt
            );
        }).collect(Collectors.toList());
    }

    private Dto.VisitRecordResponse toVisitResponse(VisitRecord v) {
        return new Dto.VisitRecordResponse(
                v.getId(),
                v.getAppointment().getId(),
                v.getAppointment().getPatient().getId(),
                v.getAppointment().getPatient().getFirstName() + " " + v.getAppointment().getPatient().getLastName(),
                v.getAppointment().getSlot().getSlotDate(),
                v.getDiagnosis(),
                v.getPrescription(),
                v.getRecordedAt()
        );
    }
}
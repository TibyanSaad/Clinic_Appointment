package com.clinic.Clinic_Appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

// ── Patient ──────────────────────────────────────────────────────────────────

public class Dto {

    //  for POST/patients to validate the invalid input
    public static class CreatePatientRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        private LocalDate birthDate;

        private List<@Size(min = 8, max = 8, message = "Phone number must be exactly 8 digits") String> phones;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

        public List<String> getPhones() { return phones; }
        public void setPhones(List<String> phones) { this.phones = phones; }
    }

    // POST/patients & GET/patients/{id}
    public static class PatientResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private List<String> phones;

        public PatientResponse(Long id, String firstName, String lastName, LocalDate birthDate, List<String> phones) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.phones = phones;
        }

        public Long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public LocalDate getBirthDate() { return birthDate; }
        public List<String> getPhones() { return phones; }
    }

    // ── Doctor ───────────────────────────────────────────────────────────────

    // POST/doctors
    public static class CreateDoctorRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Speciality is required")
        private String speciality;

        @NotNull(message = "Working hours start is required")
        private LocalTime workingHoursStart;

        @NotNull(message = "Working hours end is required")
        private LocalTime workingHoursEnd;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getSpeciality() { return speciality; }
        public void setSpeciality(String speciality) { this.speciality = speciality; }

        public LocalTime getWorkingHoursStart() { return workingHoursStart; }
        public void setWorkingHoursStart(LocalTime workingHoursStart) { this.workingHoursStart = workingHoursStart; }

        public LocalTime getWorkingHoursEnd() { return workingHoursEnd; }
        public void setWorkingHoursEnd(LocalTime workingHoursEnd) { this.workingHoursEnd = workingHoursEnd; }
    }
    // POST /doctors & GET /doctors/{id}
    public static class DoctorResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String speciality;
        private LocalTime workingHoursStart;
        private LocalTime workingHoursEnd;

        public DoctorResponse(Long id, String firstName, String lastName, String speciality,
                              LocalTime workingHoursStart, LocalTime workingHoursEnd) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.speciality = speciality;
            this.workingHoursStart = workingHoursStart;
            this.workingHoursEnd = workingHoursEnd;
        }

        public Long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getSpeciality() { return speciality; }
        public LocalTime getWorkingHoursStart() { return workingHoursStart; }
        public LocalTime getWorkingHoursEnd() { return workingHoursEnd; }
    }

    // ── Appointment ──────────────────────────────────────────────────────────

    //POST /appointments
    public static class BookAppointmentRequest {
        @NotNull(message = "Patient ID is required")
        private Long patientId;

        @NotNull(message = "Doctor ID is required")
        private Long doctorId;

        @NotNull(message = "Appointment date is required")
        @FutureOrPresent(message = "Appointment date must not be in the past")
        private LocalDate date;

        @NotNull(message = "Appointment time is required")
        private LocalTime startTime;

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    }

    //POST /appointments/{id}/reschedule
    public static class RescheduleRequest {
        @NotNull(message = "New date is required")
        @FutureOrPresent(message = "New date must not be in the past")
        private LocalDate newDate;

        @NotNull(message = "New start time is required")
        private LocalTime newStartTime;

        public LocalDate getNewDate() { return newDate; }
        public void setNewDate(LocalDate newDate) { this.newDate = newDate; }

        public LocalTime getNewStartTime() { return newStartTime; }
        public void setNewStartTime(LocalTime newStartTime) { this.newStartTime = newStartTime; }
    }
    // POST /appointments, POST /appointments/{id}/cancel, POST /appointments/{id}/reschedule & POST /appointments/{id}/complete
    public static class AppointmentResponse {
        private Long appointmentId;
        private String status;
        private Long patientId;
        private String patientName;
        private Long doctorId;
        private String doctorName;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Long rescheduledToAppointmentId;

        public AppointmentResponse(Long appointmentId, String status, Long patientId, String patientName,
                                   Long doctorId, String doctorName, LocalDate slotDate,
                                   LocalTime startTime, LocalTime endTime, Long rescheduledToAppointmentId) {
            this.appointmentId = appointmentId;
            this.status = status;
            this.patientId = patientId;
            this.patientName = patientName;
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.rescheduledToAppointmentId = rescheduledToAppointmentId;
        }

        public Long getAppointmentId() { return appointmentId; }
        public String getStatus() { return status; }
        public Long getPatientId() { return patientId; }
        public String getPatientName() { return patientName; }
        public Long getDoctorId() { return doctorId; }
        public String getDoctorName() { return doctorName; }
        public LocalDate getSlotDate() { return slotDate; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public Long getRescheduledToAppointmentId() { return rescheduledToAppointmentId; }
    }

    // ── Visit Record ─────────────────────────────────────────────────────────

    // POST /appointments/{id}/visit
    public static class RecordVisitRequest {
        @NotBlank(message = "Diagnosis is required")
        private String diagnosis;

        private String prescription;

        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

        public String getPrescription() { return prescription; }
        public void setPrescription(String prescription) { this.prescription = prescription; }
    }
    // POST /appointments/{id}/visit & GET /patients/{id}/history
    public static class VisitRecordResponse {
        private Long visitId;
        private Long appointmentId;
        private Long patientId;
        private String patientName;
        private LocalDate visitDate;
        private String diagnosis;
        private String prescription;
        private LocalDateTime recordedAt;

        public VisitRecordResponse(Long visitId, Long appointmentId, Long patientId, String patientName,
                                   LocalDate visitDate, String diagnosis, String prescription,
                                   LocalDateTime recordedAt) {
            this.visitId = visitId;
            this.appointmentId = appointmentId;
            this.patientId = patientId;
            this.patientName = patientName;
            this.visitDate = visitDate;
            this.diagnosis = diagnosis;
            this.prescription = prescription;
            this.recordedAt = recordedAt;
        }

        public Long getVisitId() { return visitId; }
        public Long getAppointmentId() { return appointmentId; }
        public Long getPatientId() { return patientId; }
        public String getPatientName() { return patientName; }
        public LocalDate getVisitDate() { return visitDate; }
        public String getDiagnosis() { return diagnosis; }
        public String getPrescription() { return prescription; }
        public LocalDateTime getRecordedAt() { return recordedAt; }
    }

    // ── Schedule ─────────────────────────────────────────────────────────────

    //GET /doctors/{id}/schedule?date=
    public static class ScheduleSlotResponse {
        private Long slotId;
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean available;
        private Long appointmentId;
        private String patientName;

        public ScheduleSlotResponse(Long slotId, LocalTime startTime, LocalTime endTime,
                                    boolean available, Long appointmentId, String patientName) {
            this.slotId = slotId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.available = available;
            this.appointmentId = appointmentId;
            this.patientName = patientName;
        }

        public Long getSlotId() { return slotId; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public boolean isAvailable() { return available; }
        public Long getAppointmentId() { return appointmentId; }
        public String getPatientName() { return patientName; }
    }

    // ── Error ─────────────────────────────────────────────────────────────────
    // GlobalExceptionHandler response
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;

        public ErrorResponse(int status, String error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }

        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
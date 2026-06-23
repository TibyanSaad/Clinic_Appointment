package com.clinic.Clinic_Appointment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "APPOINTMENT")
public class Appointment {

    public enum Status {
        SCHEDULED, CANCELLED, COMPLETED, RESCHEDULED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_APPOINTMENT_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private Status status;

    // Points to the NEW appointment this was rescheduled to
    // foreign key set
    @ManyToOne
    @JoinColumn(name = "FK_RESCHEDULE_ID")
    private Appointment rescheduledTo;

    @ManyToOne
    @JoinColumn(name = "FK_SLOT_ID", nullable = false)
    private AppointmentSlot slot;

    @ManyToOne
    @JoinColumn(name = "FK_PATIENT_ID", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "FK_DOCTOR_ID", nullable = false)
    private Doctor doctor;

    public Appointment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Appointment getRescheduledTo() { return rescheduledTo; }
    public void setRescheduledTo(Appointment rescheduledTo) { this.rescheduledTo = rescheduledTo; }

    public AppointmentSlot getSlot() { return slot; }
    public void setSlot(AppointmentSlot slot) { this.slot = slot; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}

package com.clinic.Clinic_Appointment.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VISIT_RECORDS")
public class VisitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_VISIT_ID")
    private Long id;

    @Column(name = "DIAGNOSIS", nullable = false)
    private String diagnosis;

    @Column(name = "PRESCRIPTION")
    private String prescription;

    @Column(name = "RECORDED_AT", nullable = false)
    private LocalDateTime recordedAt;

    // so each appointment points to a single visit
    @OneToOne
    @JoinColumn(name = "FK_APPOINTMENT_ID", nullable = false, unique = true)
    private Appointment appointment;

    public VisitRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
}

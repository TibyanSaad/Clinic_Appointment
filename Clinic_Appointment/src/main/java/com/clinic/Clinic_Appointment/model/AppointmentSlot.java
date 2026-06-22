package com.clinic.Clinic_Appointment.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "APPOINTMENT_SLOTS")
public class AppointmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_SLOT_ID")
    private Long id;

    @Column(name = "SLOT_DATE", nullable = false)
    private LocalDate slotDate;

    @Column(name = "START_TIME", nullable = false)
    private LocalTime startTime;

    @Column(name = "END_TIME", nullable = false)
    private LocalTime endTime;

    @Column(name = "IS_AVAILABLE", nullable = false)
    private boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "FK_DOCTOR_ID", nullable = false)
    private Doctor doctor;

    public AppointmentSlot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}

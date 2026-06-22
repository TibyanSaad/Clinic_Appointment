package com.clinic.Clinic_Appointment.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "DOCTOR")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_DOCTOR_ID")
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "SPECIALITY", nullable = false)
    private String speciality;

    @Column(name = "WORKING_HOURS_START", nullable = false)
    private LocalTime workingHoursStart;

    @Column(name = "WORKING_HOURS_END", nullable = false)
    private LocalTime workingHoursEnd;

    public Doctor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

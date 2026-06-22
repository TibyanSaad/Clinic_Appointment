package com.clinic.Clinic_Appointment.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PATIENT")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_PATIENT_ID")
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "BIRTHDATE", nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<PatientPhone> phones;

    public Patient() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public List<PatientPhone> getPhones() { return phones; }
    public void setPhones(List<PatientPhone> phones) { this.phones = phones; }
}

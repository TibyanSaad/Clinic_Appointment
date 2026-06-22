package com.clinic.Clinic_Appointment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PATIENT_PHONES")
@IdClass(PatientPhoneId.class)
public class PatientPhone {

    @Id
    @Column(name = "PK_PHONE_NUMBER")
    private String phoneNumber;

    @Id
    @ManyToOne
    @JoinColumn(name = "PK_FK_PATIENT_ID", nullable = false)
    private Patient patient;

    public PatientPhone() {}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
}

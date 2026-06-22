package com.clinic.Clinic_Appointment.model;

import java.io.Serializable;
import java.util.Objects;

public class PatientPhoneId implements Serializable {
    private String phoneNumber;
    private Long patient;

    public PatientPhoneId() {}

    public PatientPhoneId(String phoneNumber, Long patient) {
        this.phoneNumber = phoneNumber;
        this.patient = patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientPhoneId)) return false;
        PatientPhoneId that = (PatientPhoneId) o;
        return Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(patient, that.patient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, patient);
    }
}

# Clinic Appointment & Patient Records API (Java)

A REST API for managing clinic appointments, patient records, and doctor schedules.

---

## Base URL
```
http://localhost:8080
```

---

## Initial Setup

1. Configure your Oracle database credentials in `application.properties`
2. Run oracle-xe-db-hosting container in Docker
3. Load up the API.

### Recommended First Steps
```
1. Create doctors
2. Create patients
```


### Application Features
```
1. Create a doctor
2. Create a patient
3. Book an appointment
4. Complete the appointment
5. Record the visit
6. View patient history
```

---

## Endpoints

---

### Doctors

#### Create a Doctor
##### POST
```
http://localhost:8080/doctors
```
```json
{
  "firstName": "Sarah",
  "lastName": "Al-Rashdi",
  "speciality": "Cardiology",
  "workingHoursStart": "08:00",
  "workingHoursEnd": "16:00"
}
```
**Response: 201 Created**
```json
{
  "id": 1,
  "firstName": "Sarah",
  "lastName": "Al-Rashdi",
  "speciality": "Cardiology",
  "workingHoursStart": "08:00",
  "workingHoursEnd": "16:00"
}
```

---

#### Display a Doctor
##### GET
```
http://localhost:8080/doctors/{doctorId}
```
```
http://localhost:8080/doctors/1
```
**Response: 200 OK**
```json
{
  "id": 1,
  "firstName": "Sarah",
  "lastName": "Al-Rashdi",
  "speciality": "Cardiology",
  "workingHoursStart": "08:00",
  "workingHoursEnd": "16:00"
}
```

---

#### Display a Doctor's Schedule for a Day
##### GET
```
http://localhost:8080/doctors/{doctorId}/schedule?date={date}
```
```
http://localhost:8080/doctors/1/schedule?date=2026-07-10
```
**Response: 200 OK**
```json
[
  {
    "slotId": 1,
    "startTime": "09:00",
    "endTime": "09:30",
    "available": false,
    "appointmentId": 3,
    "patientName": "Ahmed Al-Balushi"
  }
]
```

---

### Patients

#### Create a Patient
##### POST
```
http://localhost:8080/patients
```
```json
{
  "firstName": "Ahmed",
  "lastName": "Al-Balushi",
  "birthDate": "1990-03-15",
  "phones": ["12345678", "87654321"]
}
```
> `phones` field is optional.

**Response: 201 Created**
```json
{
  "id": 1,
  "firstName": "Ahmed",
  "lastName": "Al-Balushi",
  "birthDate": "1990-03-15",
  "phones": ["12345678", "87654321"]
}
```

---

#### Display a Patient
##### GET
```
http://localhost:8080/patients/{patientId}
```
```
http://localhost:8080/patients/1
```
**Response: 200 OK**
```json
{
  "id": 1,
  "firstName": "Ahmed",
  "lastName": "Al-Balushi",
  "birthDate": "1990-03-15",
  "phones": ["12345678", "87654321"]
}
```

---

#### Display a Patient's Full Visit History
##### GET
```
http://localhost:8080/patients/{patientId}/history
```
```
http://localhost:8080/patients/1/history
```
**Response: 200 OK**
```json
[
  {
    "visitId": 1,
    "appointmentId": 3,
    "patientId": 1,
    "patientName": "Ahmed Al-Balushi",
    "visitDate": "2026-07-10",
    "diagnosis": "Hypertension Stage 1",
    "prescription": "Amlodipine 5mg once daily for 30 days",
    "recordedAt": "2026-07-10T11:45:00"
  }
]
```

---

### Appointments

#### Book an Appointment
##### POST
```
http://localhost:8080/appointments
```
```json
{
  "patientId": 1,
  "doctorId": 1,
  "date": "2026-07-10",
  "startTime": "09:00"
}
```
> Once the start time is provided a 30-minute slot is automatically generated from the start time

**Response: 201 Created**
```json
{
  "appointmentId": 1,
  "status": "SCHEDULED",
  "patientId": 1,
  "patientName": "Ahmed Al-Balushi",
  "doctorId": 1,
  "doctorName": "Sarah Al-Rashdi",
  "slotDate": "2026-07-10",
  "startTime": "09:00",
  "endTime": "09:30",
  "rescheduledToAppointmentId": null
}
```

---

#### Cancel an Appointment
##### POST
```
http://localhost:8080/appointments/{appointmentId}/cancel
```
```
http://localhost:8080/appointments/1/cancel
```
> No request body needed. Appointment must be `SCHEDULED`.

**Response: 200 OK**
```json
{
  "appointmentId": 1,
  "status": "CANCELLED",
  "patientId": 1,
  "patientName": "Ahmed Al-Balushi",
  "doctorId": 1,
  "doctorName": "Sarah Al-Rashdi",
  "slotDate": "2026-07-10",
  "startTime": "09:00",
  "endTime": "09:30",
  "rescheduledToAppointmentId": null
}
```

---

#### Reschedule an Appointment
##### POST
```
http://localhost:8080/appointments/{appointmentId}/reschedule
```
```
http://localhost:8080/appointments/2/reschedule
```
```json
{
  "newDate": "2026-07-15",
  "newStartTime": "11:00"
}
```
> Appointment must be `SCHEDULED`. The old appointment becomes `RESCHEDULED` and a new `SCHEDULED` appointment is created, to preserve history.

**Response: 200 OK**
```json
{
  "appointmentId": 2,
  "status": "SCHEDULED",
  "patientId": 1,
  "patientName": "Ahmed Al-Balushi",
  "doctorId": 1,
  "doctorName": "Sarah Al-Rashdi",
  "slotDate": "2026-07-15",
  "startTime": "11:00",
  "endTime": "11:30",
  "rescheduledToAppointmentId": null
}
```

---

#### Complete an Appointment
##### POST
```
http://localhost:8080/appointments/{appointmentId}/complete
```
```
http://localhost:8080/appointments/2/complete
```
> No request body needed. Appointment must be `SCHEDULED`.

**Response: 200 OK**
```json
{
  "appointmentId": 2,
  "status": "COMPLETED",
  "patientId": 1,
  "patientName": "Ahmed Al-Balushi",
  "doctorId": 1,
  "doctorName": "Sarah Al-Rashdi",
  "slotDate": "2026-07-15",
  "startTime": "11:00",
  "endTime": "11:30",
  "rescheduledToAppointmentId": null
}
```

---

#### Record Diagnosis & Prescription
##### POST

> An appointment must be set as completed before recording a diagnosis or prescription
```
http://localhost:8080/appointments/{appointmentId}/visit
```
```
http://localhost:8080/appointments/1/visit
```
```json
{
  "diagnosis": "Hypertension Stage 1",
  "prescription": "Amlodipine 5mg once daily for 30 days"
}
```
> `prescription` is optional.

**Response: 201 Created**
```json
{
  "visitId": 1,
  "appointmentId": 2,
  "patientId": 1,
  "patientName": "Ahmed Al-Balushi",
  "visitDate": "2026-07-15",
  "diagnosis": "Hypertension Stage 1",
  "prescription": "Amlodipine 5mg once daily for 30 days",
  "recordedAt": "2026-07-15T11:45:00"
}
```

---

## Business Rules

| Rule                           | Details                                                                                        |
|--------------------------------|------------------------------------------------------------------------------------------------|
| Doctor working hours           | Start must be before end                                                                       |
| Appointment date               | Cannot be in the past                                                                          |
| Appointment time               | Must fall within doctor's working hours                                                        |
| Doctor conflict                | Doctor cannot have two overlapping appointments                                                |
| Patient conflict (same doctor) | Patient cannot have two active appointments with the same doctor on the same day               |
| Patient conflict (any doctor)  | Patient cannot be booked with any doctor in an overlapping time range with another appointment |
| Complete before visit          | Appointment must be `COMPLETED` before a visit record can be added                             |
| One visit per appointment      | Cannot record two visits for the same appointment                                              |
| Scheduled appointments         | Only `SCHEDULED` appointments can be cancelled, rescheduled, or completed                      |
| Booking/rescheduling conflict  | patients are not able to book or reschedule in between a pre-existing appointment time slot.   |

---
## Business Conflict Violations


### 409 Errors

#### Doctor is already booked in that time range
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Doctor is already booked between 09:00 and 09:30 on 2026-07-10"
}
```

#### Patient already has an appointment with the same doctor on the same day
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Patient already has an active appointment with this doctor on 2026-07-10"
}
```

#### Patient is already booked with another doctor in the same time range
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Patient already has an appointment with another doctor between 09:00 and 09:30 on 2026-07-10"
}
```
#### Trying to record a second visit for the same appointment
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "A visit record already exists for appointment ID: 5"
}
```

### 404 Errors
#### Trying to cancel an appointment that is not SCHEDULED
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Only SCHEDULED appointments can be cancelled. Current status: COMPLETED"
}
```

#### Trying to reschedule an appointment that is not SCHEDULED
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Only SCHEDULED appointments can be rescheduled. Current status: CANCELLED"
}
```

#### Trying to complete an appointment that is not SCHEDULED
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Only SCHEDULED appointments can be marked as completed. Current status: RESCHEDULED"
}
```

#### Trying to record a visit on an appointment that is not COMPLETED
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Visit can only be recorded for COMPLETED appointments. Current status: SCHEDULED"
}
```
#### Doctor outside working hours
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Requested time 07:00 is outside the doctor's working hours (08:00 - 16:00)"
}
```



---
## Error Responses

| Status Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 400 | Bad request or invalid input |
| 404 | Resource not found |
| 409 | Conflict (double booking, duplicate visit, etc.) |
| 500 | Unexpected server error |

---

## Appointment Status Flow

```
SCHEDULED ──► CANCELLED
     │
     ├──► RESCHEDULED (old) ──► new SCHEDULED appointment created
     │
     └──► COMPLETED ──► visit record can now be added
```

> No appointment or slot is ever deleted from the database. All history is preserved.
# Change Log

## Version[0.0.4] - 23-06-2026


## Version[0.0.3] - 22-06-2026
### Changed
- `AppointmentSlot.java`: removed `isAvailable` field. Slots now only exist in the table when an active appointment occupies them, so availability is determined by the slot's existence and the appointment's status rather than a boolean flag.
- `SlotService.java`: removed `generateSlots()` — slots are no longer pre-generated for all 30-minute intervals within working hours. `createSlot()` is now the only entry point for slot creation and is called at booking time. Added overlap conflict check using `existsByDoctorIdAndSlotDateAndTimeOverlap()` to reject any booking whose requested time falls within an existing active slot's 30-minute window (e.g. a request for 09:05 is rejected if an active slot runs 09:00–09:30). Removed `markAvailable()` and `markUnavailable()` helpers; cancellation and rescheduling now delete the slot row instead of toggling a flag.
- `AppointmentSlotRepository.java`: replaced `existsByDoctorIdAndSlotDateAndStartTimeAndIsAvailableFalse()` with a JPQL overlap query `existsByDoctorIdAndSlotDateAndTimeOverlap()` that checks whether a requested start time falls within any active slot's `[startTime, endTime)` range for that doctor on that date.
- `AppointmentService.java`: updated `cancelAppointment()` and `rescheduleAppointment()` to delete the old slot row via `slotRepository.delete()` rather than marking it available, keeping the `APPOINTMENT_SLOTS` table lean — only actively booked slots are stored at any time.
- `DoctorController.java`: removed `POST /doctors/{doctorId}/slots` endpoint since slot generation is no longer a manual step.

## Version[0.0.2] - 22-06-2026
### Fixed
- `AppointmentService.java`: added `completeAppointment()` method to transition a BOOKED appointment to COMPLETED status, enabling visit recording. 
-  Added validation to reject completion of non-BOOKED appointments.
- `AppointmentController.java`: added `PATCH /appointments/{appointmentId}/complete` endpoint wired to `completeAppointment()` in the service.

## Version[0.0.1] - 22-06-2026
### Added
- Initial release of Clinic Appointment and Patient Records API.
- `Doctor.java`, `Patient.java`, `AppointmentSlot.java`, `Appointment.java`, `VisitRecord.java`: core domain models with JPA annotations. `Appointment` includes self-referencing `rescheduledTo` field and `AppointmentStatus` enum (BOOKED, CANCELLED, COMPLETED, RESCHEDULED).
- `DoctorRepository.java`, `PatientRepository.java`, `AppointmentSlotRepository.java`, `AppointmentRepository.java`, `VisitRecordRepository.java`: repository interfaces. `AppointmentRepository` includes custom JPQL queries for active appointment conflict check and full patient visit history retrieval.
- `DoctorService.java`: doctor creation with working hours validation, 30-minute slot generation within working hours, and schedule view showing booked vs free slots.
- `PatientService.java`: patient creation and lookup.
- `AppointmentService.java`: booking with slot availability and one-active-appointment-per-doctor-per-day enforcement, cancellation and rescheduling without row deletion (status update only), visit recording restricted to COMPLETED past appointments.
- `DoctorController.java`, `PatientController.java`, `AppointmentController.java`: REST controllers with full validation on all request bodies.
- `ResourceNotFoundException.java`, `BusinessRuleException.java`: custom exceptions for 404 and 409 responses respectively.
- `GlobalExceptionHandler.java`: centralised error handling returning structured `ErrorResponse` with status, error, message, timestamp and details — no stack traces exposed.
- Request and response DTOs for all entities with `@Valid` annotations on all input fields.
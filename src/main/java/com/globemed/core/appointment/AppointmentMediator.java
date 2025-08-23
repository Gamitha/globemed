package com.globemed.core.appointment;

import com.globemed.core.model.Patient;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mediator pattern implementation for coordinating appointment scheduling between
 * different participants (patients, doctors, appointments)
 */
public interface AppointmentMediator {
    void scheduleAppointment(UUID patientId, String doctorId, LocalDateTime dateTime, String notes);
    void confirmAppointment(UUID appointmentId);
    void completeAppointment(UUID appointmentId);
    void cancelAppointment(UUID appointmentId);
    boolean checkAvailability(String doctorId, LocalDateTime dateTime);
    Patient getPatient(UUID patientId);
}

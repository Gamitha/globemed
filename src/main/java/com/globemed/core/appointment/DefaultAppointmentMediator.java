package com.globemed.core.appointment;

import com.globemed.core.controller.AppointmentController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class DefaultAppointmentMediator implements AppointmentMediator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAppointmentMediator.class);
    private final AppointmentController appointmentController;
    private final PatientController patientController;

    public DefaultAppointmentMediator(AppointmentController appointmentController, PatientController patientController) {
        this.appointmentController = appointmentController;
        this.patientController = patientController;
    }

    @Override
    public void scheduleAppointment(UUID patientId, String doctorId, LocalDateTime dateTime, String notes) {
        Optional<Patient> patient = getPatient(patientId);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }

        if (!checkAvailability(doctorId, dateTime)) {
            throw new IllegalStateException("Doctor is not available at the specified time");
        }

        Appointment appointment = new Appointment.Builder()
            .withPatientId(patientId)
            .withDoctorId(doctorId)
            .withDateTime(dateTime)
            .withNotes(notes)
            .build();

        appointmentController.scheduleAppointment(appointment);
    }

    @Override
    public void confirmAppointment(UUID appointmentId) {
        appointmentController.confirmAppointment(appointmentId);
        logger.info("Confirmed appointment: {}", appointmentId);
    }

    @Override
    public void completeAppointment(UUID appointmentId) {
        appointmentController.completeAppointment(appointmentId);
        logger.info("Completed appointment: {}", appointmentId);
    }

    @Override
    public void cancelAppointment(UUID appointmentId) {
        appointmentController.cancelAppointment(appointmentId);
        logger.info("Cancelled appointment: {}", appointmentId);
    }

    @Override
    public boolean checkAvailability(String doctorId, LocalDateTime dateTime) {
        return appointmentController.getAllAppointments().stream()
            .filter(a -> a.getDoctorId().equals(doctorId))
            .noneMatch(a -> {
                LocalDateTime scheduledTime = a.getScheduledDateTime();
                // Check if the appointment overlaps with the requested time
                return scheduledTime != null && scheduledTime.equals(dateTime);
            });
    }

    @Override
    public Optional<Patient> getPatient(UUID patientId) {
        return patientController.getPatient(patientId.toString());
    }

    @Override
    public void notifyAppointmentUpdated(Appointment appointment) {
        getPatient(appointment.getPatientId()).ifPresent(patient -> {
            logger.info("Notified patient {} about appointment update", patient.getId());
        });
    }

    @Override
    public void notifyAppointmentCancelled(Appointment appointment) {
        getPatient(appointment.getPatientId()).ifPresent(patient -> {
            logger.info("Notified patient {} about appointment cancellation", patient.getId());
        });
    }
}

package com.globemed.core.appointment;

import com.globemed.core.controller.AppointmentController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.model.Patient;

import java.time.LocalDateTime;
import java.util.UUID;

public class DefaultAppointmentMediator implements AppointmentMediator {
    private final AppointmentController appointmentController;
    private final PatientController patientController;

    public DefaultAppointmentMediator(AppointmentController appointmentController, PatientController patientController) {
        this.appointmentController = appointmentController;
        this.patientController = patientController;
    }

    @Override
    public void scheduleAppointment(UUID patientId, String doctorId, LocalDateTime dateTime, String notes) {
        // Validate patient exists
        Patient patient = getPatient(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }

        // Check doctor availability
        if (!checkAvailability(doctorId, dateTime)) {
            throw new IllegalStateException("Doctor is not available at the specified time");
        }

        // Create and schedule appointment
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
    }

    @Override
    public void completeAppointment(UUID appointmentId) {
        appointmentController.completeAppointment(appointmentId);
    }

    @Override
    public void cancelAppointment(UUID appointmentId) {
        appointmentController.cancelAppointment(appointmentId);
    }

    @Override
    public boolean checkAvailability(String doctorId, LocalDateTime dateTime) {
        // Check if doctor has any conflicting appointments
        return appointmentController.getAllAppointments().stream()
            .filter(a -> a.getDoctorId().equals(doctorId))
            .noneMatch(a -> a.getDateTime().equals(dateTime));
    }

    @Override
    public Patient getPatient(UUID patientId) {
        return patientController.getPatient(patientId).orElse(null);
    }
}

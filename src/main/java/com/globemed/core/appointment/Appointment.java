package com.globemed.core.appointment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID patientId;
    private String doctorId;
    private LocalDateTime requestedDateTime;  // When the appointment was initially requested
    private LocalDateTime scheduledDateTime;  // The actual scheduled time of the appointment
    private LocalDateTime confirmedDateTime;  // When the appointment was confirmed
    private LocalDateTime completedDateTime;  // When the appointment was completed
    private LocalDateTime cancelledDateTime;  // When the appointment was cancelled (if applicable)
    private AppointmentState state;
    private String notes;

    private Appointment() {
        this.id = UUID.randomUUID();
        this.state = new RequestedState();  // Initial state
        this.requestedDateTime = LocalDateTime.now();
    }

    // State pattern methods
    public void setState(AppointmentState state) {
        this.state = state;

        // Update state-specific timestamps
        if (state instanceof ConfirmedState) {
            this.confirmedDateTime = LocalDateTime.now();
        } else if (state instanceof CompletedState) {
            this.completedDateTime = LocalDateTime.now();
        } else if (state instanceof CancelledState) {
            this.cancelledDateTime = LocalDateTime.now();
        }
    }

    public void confirm() {
        state.confirm(this);
    }

    public void complete() {
        state.complete(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public String getStatus() {
        return state.getStatus();
    }

    // Builder Pattern
    public static class Builder implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Appointment appointment;

        public Builder() {
            appointment = new Appointment();
        }

        public Builder withPatientId(UUID patientId) {
            appointment.patientId = patientId;
            return this;
        }

        public Builder withDoctorId(String doctorId) {
            appointment.doctorId = doctorId;
            return this;
        }

        public Builder withDateTime(LocalDateTime dateTime) {
            appointment.scheduledDateTime = dateTime;
            return this;
        }

        public Builder withNotes(String notes) {
            appointment.notes = notes;
            return this;
        }

        public Appointment build() {
            if (appointment.patientId == null || appointment.doctorId == null || appointment.scheduledDateTime == null) {
                throw new IllegalStateException("Patient ID, Doctor ID, and DateTime are required");
            }
            return appointment;
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDateTime getRequestedDateTime() { return requestedDateTime; }
    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }
    public LocalDateTime getConfirmedDateTime() { return confirmedDateTime; }
    public LocalDateTime getCompletedDateTime() { return completedDateTime; }
    public LocalDateTime getCancelledDateTime() { return cancelledDateTime; }
    public String getNotes() { return notes; }

    public LocalDateTime getCurrentStateDateTime() {
        if (cancelledDateTime != null) return cancelledDateTime;
        if (completedDateTime != null) return completedDateTime;
        if (confirmedDateTime != null) return confirmedDateTime;
        return requestedDateTime;
    }
}

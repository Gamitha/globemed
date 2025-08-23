package com.globemed.core.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public class Appointment {
    private UUID id;
    private UUID patientId;
    private String doctorId;
    private LocalDateTime dateTime;
    private AppointmentState state;
    private String notes;

    private Appointment() {
        this.id = UUID.randomUUID();
        this.state = new RequestedState();  // Initial state
    }

    // State pattern methods
    public void setState(AppointmentState state) {
        this.state = state;
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
    public static class Builder {
        private Appointment appointment;

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
            appointment.dateTime = dateTime;
            return this;
        }

        public Builder withNotes(String notes) {
            appointment.notes = notes;
            return this;
        }

        public Appointment build() {
            if (appointment.patientId == null ||
                appointment.doctorId == null ||
                appointment.dateTime == null) {
                throw new IllegalStateException("Appointment requires patientId, doctorId, and dateTime");
            }
            return appointment;
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getNotes() { return notes; }
}

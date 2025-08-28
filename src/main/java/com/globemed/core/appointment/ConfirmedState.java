package com.globemed.core.appointment;

public class ConfirmedState implements AppointmentState {
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Appointment appointment) {
        throw new IllegalStateException("Appointment is already confirmed");
    }

    @Override
    public void complete(Appointment appointment) {
        if (appointment.getScheduledDateTime().isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("Cannot complete an appointment before its scheduled time");
        }
        appointment.setState(new CompletedState());
    }

    @Override
    public void cancel(Appointment appointment) {
        // Can only cancel if the appointment hasn't happened yet
        if (appointment.getScheduledDateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel an appointment after its scheduled time");
        }
        appointment.setState(new CancelledState());
    }

    @Override
    public String getStatus() {
        return "CONFIRMED";
    }
}

package com.globemed.core.appointment;

public class CompletedState implements AppointmentState {
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Appointment appointment) {
        throw new IllegalStateException("Cannot confirm a completed appointment");
    }

    @Override
    public void complete(Appointment appointment) {
        throw new IllegalStateException("Appointment is already completed");
    }

    @Override
    public void cancel(Appointment appointment) {
        throw new IllegalStateException("Cannot cancel a completed appointment");
    }

    @Override
    public String getStatus() {
        return "COMPLETED";
    }
}

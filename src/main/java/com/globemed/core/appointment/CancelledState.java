package com.globemed.core.appointment;

public class CancelledState implements AppointmentState {
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Appointment appointment) {
        throw new IllegalStateException("Cannot confirm a cancelled appointment");
    }

    @Override
    public void complete(Appointment appointment) {
        throw new IllegalStateException("Cannot complete a cancelled appointment");
    }

    @Override
    public void cancel(Appointment appointment) {
        throw new IllegalStateException("Appointment is already cancelled");
    }

    @Override
    public String getStatus() {
        return "CANCELLED";
    }
}

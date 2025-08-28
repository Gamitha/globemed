package com.globemed.core.appointment;

public class RequestedState implements AppointmentState {
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Appointment appointment) {
        appointment.setState(new ConfirmedState());
    }

    @Override
    public void complete(Appointment appointment) {
        throw new IllegalStateException("Cannot complete a requested appointment. It must be confirmed first.");
    }

    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
    }

    @Override
    public String getStatus() {
        return "REQUESTED";
    }
}

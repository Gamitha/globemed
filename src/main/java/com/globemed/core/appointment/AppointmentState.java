package com.globemed.core.appointment;

public interface AppointmentState {
    void confirm(Appointment appointment);
    void complete(Appointment appointment);
    void cancel(Appointment appointment);
    String getStatus();
}

class RequestedState implements AppointmentState {
    @Override
    public void confirm(Appointment appointment) {
        appointment.setState(new ConfirmedState());
    }

    @Override
    public void complete(Appointment appointment) {
        throw new IllegalStateException("Cannot complete a requested appointment");
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

class ConfirmedState implements AppointmentState {
    @Override
    public void confirm(Appointment appointment) {
        throw new IllegalStateException("Appointment is already confirmed");
    }

    @Override
    public void complete(Appointment appointment) {
        appointment.setState(new CompletedState());
    }

    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
    }

    @Override
    public String getStatus() {
        return "CONFIRMED";
    }
}

class CompletedState implements AppointmentState {
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

class CancelledState implements AppointmentState {
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

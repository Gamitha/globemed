package com.globemed.core.appointment;

import java.io.Serializable;

/**
 * Defines the state interface for appointments using the State pattern.
 * Valid state transitions are:
 * - REQUESTED -> CONFIRMED or CANCELLED
 * - CONFIRMED -> COMPLETED or CANCELLED
 * - COMPLETED -> (no further transitions)
 * - CANCELLED -> (no further transitions)
 */
public interface AppointmentState extends Serializable {
    /**
     * Confirms an appointment. Only valid from REQUESTED state.
     * @param appointment The appointment to confirm
     * @throws IllegalStateException if confirmation is not allowed in current state
     */
    void confirm(Appointment appointment);

    /**
     * Marks an appointment as completed. Only valid from CONFIRMED state.
     * @param appointment The appointment to complete
     * @throws IllegalStateException if completion is not allowed in current state
     */
    void complete(Appointment appointment);

    /**
     * Cancels an appointment. Valid from REQUESTED or CONFIRMED states.
     * @param appointment The appointment to cancel
     * @throws IllegalStateException if cancellation is not allowed in current state
     */
    void cancel(Appointment appointment);

    /**
     * Gets the current status of the appointment.
     * @return String representation of the current state
     */
    String getStatus();
}

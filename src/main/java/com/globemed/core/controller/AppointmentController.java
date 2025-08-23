package com.globemed.core.controller;

import com.globemed.core.appointment.Appointment;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppointmentController {
    private final SecurityService securityService;
    private final Map<UUID, Appointment> appointments;

    public AppointmentController(SecurityService securityService) {
        this.securityService = securityService;
        this.appointments = new ConcurrentHashMap<>();
    }

    public void scheduleAppointment(Appointment appointment) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:create",
            Set.of(Permission.WRITE)
        );
        appointments.put(appointment.getId(), appointment);
    }

    public void confirmAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:update",
            Set.of(Permission.WRITE)
        );
        Optional.ofNullable(appointments.get(appointmentId))
                .ifPresent(Appointment::confirm);
    }

    public void completeAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:update",
            Set.of(Permission.WRITE)
        );
        Optional.ofNullable(appointments.get(appointmentId))
                .ifPresent(Appointment::complete);
    }

    public void cancelAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:update",
            Set.of(Permission.WRITE)
        );
        Optional.ofNullable(appointments.get(appointmentId))
                .ifPresent(Appointment::cancel);
    }

    public Optional<Appointment> getAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return Optional.ofNullable(appointments.get(appointmentId));
    }

    public List<Appointment> getAllAppointments() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return new ArrayList<>(appointments.values());
    }
}

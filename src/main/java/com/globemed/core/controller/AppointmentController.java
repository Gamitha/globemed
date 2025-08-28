package com.globemed.core.controller;

import com.globemed.core.appointment.Appointment;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import com.globemed.core.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.*;

public class AppointmentController {
    private final SecurityService securityService;
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(SecurityService securityService) {
        this.securityService = securityService;
        this.appointmentRepository = new AppointmentRepository();
    }

    public void scheduleAppointment(Appointment appointment) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:create",
            Set.of(Permission.WRITE)
        );
        appointmentRepository.save(appointment);
    }

    public void confirmAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:update",
            Set.of(Permission.WRITE)
        );
        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.confirm();
            appointmentRepository.save(appointment);
        });
    }

    public void completeAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:update",
            Set.of(Permission.WRITE)
        );
        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.complete();
            appointmentRepository.save(appointment);
        });
    }

    public void cancelAppointment(UUID appointmentId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:update",
            Set.of(Permission.WRITE)
        );
        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.cancel();
            appointmentRepository.save(appointment);
        });
    }

    public Optional<Appointment> getAppointment(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAllAppointments() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByDoctor(UUID doctorId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatient(UUID patientId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:read",
            Set.of(Permission.READ)
        );
        return appointmentRepository.findByDateRange(start, end);
    }

    public void deleteAppointment(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "appointment:delete",
            Set.of(Permission.WRITE)
        );
        appointmentRepository.delete(id);
    }
}

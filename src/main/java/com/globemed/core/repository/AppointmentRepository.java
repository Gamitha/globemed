package com.globemed.core.repository;

import com.globemed.core.appointment.Appointment;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentRepository {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentRepository.class);
    private final DB db;
    private final BTreeMap<String, byte[]> appointmentMap;

    public AppointmentRepository() {
        this.db = DatabaseConfig.getDatabase();
        this.appointmentMap = db.treeMap("appointments")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
                .createOrOpen();
    }

    public void save(Appointment appointment) {
        try {
            byte[] serializedAppointment = serializeAppointment(appointment);
            appointmentMap.put(appointment.getId().toString(), serializedAppointment);
            db.commit();
            logger.info("Appointment saved successfully: {}", appointment.getId());
        } catch (Exception e) {
            logger.error("Failed to save appointment: {}. Error: {}", appointment.getId(), e.getMessage());
            throw new RuntimeException("Failed to save appointment: " + e.getMessage(), e);
        }
    }

    public Optional<Appointment> findById(UUID id) {
        try {
            byte[] serializedAppointment = appointmentMap.get(id.toString());
            if (serializedAppointment == null) {
                return Optional.empty();
            }
            return Optional.of(deserializeAppointment(serializedAppointment));
        } catch (Exception e) {
            logger.error("Failed to retrieve appointment: {}", id, e);
            throw new RuntimeException("Failed to retrieve appointment", e);
        }
    }

    public List<Appointment> findByDoctorId(UUID doctorId) {
        return findAll().stream()
                .filter(appointment -> appointment.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByPatientId(UUID patientId) {
        return findAll().stream()
                .filter(appointment -> appointment.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return findAll().stream()
                .filter(appointment -> {
                    LocalDateTime appointmentTime = appointment.getScheduledDateTime();
                    return appointmentTime != null &&
                           !appointmentTime.isBefore(start) &&
                           !appointmentTime.isAfter(end);
                })
                .sorted(Comparator.comparing(Appointment::getScheduledDateTime))
                .collect(Collectors.toList());
    }

    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            List<byte[]> serializedAppointments = new ArrayList<>(appointmentMap.values());
            for (byte[] serializedAppointment : serializedAppointments) {
                if (serializedAppointment != null) {
                    appointments.add(deserializeAppointment(serializedAppointment));
                }
            }
            return appointments.stream()
                    .sorted(Comparator.comparing(Appointment::getScheduledDateTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve all appointments", e);
            throw new RuntimeException("Failed to retrieve all appointments", e);
        }
    }

    public void delete(UUID id) {
        try {
            appointmentMap.remove(id.toString());
            db.commit();
            logger.info("Appointment deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete appointment: {}", id, e);
            throw new RuntimeException("Failed to delete appointment", e);
        }
    }

    private byte[] serializeAppointment(Appointment appointment) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos) {
                @Override
                protected void writeStreamHeader() {
                    // Skip stream header to avoid issues with MapDB
                }
            }) {
                oos.writeObject(appointment);
            }
            return bos.toByteArray();
        }
    }

    private Appointment deserializeAppointment(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            try (ObjectInputStream ois = new ObjectInputStream(bis) {
                @Override
                protected void readStreamHeader() {
                    // Skip stream header to avoid issues with MapDB
                }
            }) {
                return (Appointment) ois.readObject();
            }
        }
    }
}

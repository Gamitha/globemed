package com.globemed.core.repository;

import com.globemed.core.model.Doctor;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class DoctorRepository {
    private static final Logger logger = LoggerFactory.getLogger(DoctorRepository.class);
    private final DB db;
    private final BTreeMap<String, byte[]> doctorMap;
    private final SecurityService securityService;

    public DoctorRepository(SecurityService securityService) {
        this.db = DatabaseConfig.getDatabase();
        this.securityService = securityService;
        this.doctorMap = db.treeMap("doctors")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
                .createOrOpen();
    }

    public void save(Doctor doctor) {
        // System context always has write permission
        if (!securityService.getCurrentUser().equals("system") && !securityService.hasPermission(Permission.WRITE)) {
            throw new SecurityException("Insufficient permissions to save doctor data");
        }

        try {
            byte[] serializedDoctor = serializeDoctor(doctor);
            doctorMap.put(doctor.getId().toString(), serializedDoctor);
            db.commit();
            logger.info("Doctor saved successfully: {} - Dr. {} {}",
                doctor.getId(), doctor.getFirstName(), doctor.getLastName());
        } catch (Exception e) {
            logger.error("Failed to save doctor: {}. Error: {}", doctor.getId(), e.getMessage());
            throw new RuntimeException("Failed to save doctor: " + e.getMessage(), e);
        }
    }

    public Optional<Doctor> findById(String id) {
        if (!securityService.hasPermission(Permission.READ)) {
            throw new SecurityException("Insufficient permissions to read doctor data");
        }

        try {
            byte[] serializedDoctor = doctorMap.get(id);
            if (serializedDoctor == null) {
                return Optional.empty();
            }
            Doctor doctor = deserializeDoctor(serializedDoctor);
            return Optional.of(doctor);
        } catch (Exception e) {
            logger.error("Failed to retrieve doctor: {}", id, e);
            throw new RuntimeException("Failed to retrieve doctor", e);
        }
    }

    public List<Doctor> findAll() {
        if (!securityService.hasPermission(Permission.READ)) {
            throw new SecurityException("Insufficient permissions to read doctor data");
        }

        List<Doctor> doctors = new ArrayList<>();
        try {
            List<byte[]> serializedDoctors = new ArrayList<>(doctorMap.values());
            for (byte[] serializedDoctor : serializedDoctors) {
                if (serializedDoctor != null) {
                    doctors.add(deserializeDoctor(serializedDoctor));
                }
            }
            return doctors.stream()
                    .sorted(Comparator.comparing(Doctor::getLastName)
                            .thenComparing(Doctor::getFirstName))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve all doctors", e);
            throw new RuntimeException("Failed to retrieve all doctors", e);
        }
    }

    public void delete(String id) {
        if (!securityService.hasPermission(Permission.DELETE)) {
            throw new SecurityException("Insufficient permissions to delete doctor data");
        }

        try {
            Doctor doctor = findById(id).orElse(null);
            if (doctor != null) {
                doctorMap.remove(id);
                db.commit();
                logger.info("Doctor deleted successfully: {} - Dr. {} {}",
                    id, doctor.getFirstName(), doctor.getLastName());
            }
        } catch (Exception e) {
            logger.error("Failed to delete doctor: {}. Error: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete doctor: " + e.getMessage(), e);
        }
    }

    private byte[] serializeDoctor(Doctor doctor) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos) {
                @Override
                protected void writeStreamHeader() {
                    // Skip stream header to avoid issues with MapDB
                }
            }) {
                oos.writeObject(doctor);
            }
            return bos.toByteArray();
        }
    }

    private Doctor deserializeDoctor(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            try (ObjectInputStream ois = new ObjectInputStream(bis) {
                @Override
                protected void readStreamHeader() {
                    // Skip stream header to avoid issues with MapDB
                }
            }) {
                return (Doctor) ois.readObject();
            }
        }
    }
}

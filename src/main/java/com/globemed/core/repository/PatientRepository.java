package com.globemed.core.repository;

import com.globemed.core.model.Patient;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PatientRepository {
    private static final Logger logger = LoggerFactory.getLogger(PatientRepository.class);
    private final DB db;
    private final BTreeMap<String, byte[]> patientMap;
    private final SecurityService securityService;

    public PatientRepository(SecurityService securityService) {
        this.db = DatabaseConfig.getDatabase();
        this.securityService = securityService;
        this.patientMap = db.treeMap("patients")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
                .createOrOpen();
    }

    public void save(Patient patient) {
        // System context always has write permission
        if (!securityService.getCurrentUser().equals("system") && !securityService.hasPermission(Permission.WRITE)) {
            throw new SecurityException("Insufficient permissions to save patient data");
        }

        try {
            byte[] serializedPatient = serializePatient(patient);
            patientMap.put(patient.getId().toString(), serializedPatient);
            // Commit after successful serialization and put operation
            db.commit();
            logger.info("Patient saved successfully: {}", patient.getId());
        } catch (Exception e) {
            // Log the error but don't attempt to rollback
            logger.error("Failed to save patient: {}. Error: {}", patient.getId(), e.getMessage());
            throw new RuntimeException("Failed to save patient: " + e.getMessage(), e);
        }
    }

    public Optional<Patient> findById(String id) {
        // System context always has read permission
        if (!securityService.getCurrentUser().equals("system") && !securityService.hasPermission(Permission.READ)) {
            throw new SecurityException("Insufficient permissions to read patient data");
        }

        try {
            byte[] serializedPatient = patientMap.get(id);
            if (serializedPatient == null) {
                return Optional.empty();
            }
            Patient patient = deserializePatient(serializedPatient);
            return Optional.of(patient);
        } catch (Exception e) {
            logger.error("Failed to retrieve patient: {}", id, e);
            throw new RuntimeException("Failed to retrieve patient", e);
        }
    }

    public List<Patient> findAll() {
        // System context always has read permission
        if (!securityService.getCurrentUser().equals("system") && !securityService.hasPermission(Permission.READ)) {
            throw new SecurityException("Insufficient permissions to read patient data");
        }

        List<Patient> patients = new ArrayList<>();
        try {
            for (byte[] serializedPatient : patientMap.values()) {
                patients.add(deserializePatient(serializedPatient));
            }
            return patients;
        } catch (Exception e) {
            logger.error("Failed to retrieve all patients", e);
            throw new RuntimeException("Failed to retrieve all patients", e);
        }
    }

    public void delete(String id) {
        if (!securityService.hasPermission(Permission.DELETE)) {
            throw new SecurityException("Insufficient permissions to delete patient data");
        }

        try {
            patientMap.remove(id);
            db.commit();
            logger.info("Patient deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete patient: {}. Error: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete patient: " + e.getMessage(), e);
        }
    }

    public boolean existsById(String id) {
        try {
            return patientMap.containsKey(id);
        } catch (Exception e) {
            logger.error("Error checking patient existence: {}", id, e);
            return false;
        }
    }

    // Update getPatientDisplayMap method that was previously in controller
    public Map<String, UUID> getPatientDisplayMap() {
        if (!securityService.hasPermission(Permission.READ)) {
            throw new SecurityException("Insufficient permissions to read patient data");
        }

        Map<String, UUID> displayMap = new LinkedHashMap<>();
        try {
            for (Patient patient : findAll()) {
                displayMap.put(
                    String.format("%s, %s (ID: %s)",
                        patient.getLastName(),
                        patient.getFirstName(),
                        patient.getId().toString().substring(0, 8)),
                    patient.getId()
                );
            }
            return displayMap;
        } catch (Exception e) {
            logger.error("Failed to create patient display map", e);
            throw new RuntimeException("Failed to create patient display map", e);
        }
    }

    private byte[] serializePatient(Patient patient) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos) {
                @Override
                protected void writeStreamHeader() {
                    // Skip stream header to avoid issues with MapDB
                }
            }) {
                oos.writeObject(patient);
            }
            return bos.toByteArray();
        }
    }

    private Patient deserializePatient(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            try (ObjectInputStream ois = new ObjectInputStream(bis) {
                @Override
                protected void readStreamHeader() {
                    // Skip stream header to avoid issues with MapDB
                }
            }) {
                return (Patient) ois.readObject();
            }
        }
    }
}

package com.globemed.core.controller;

import com.globemed.core.model.Patient;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import com.globemed.core.util.DataChangeListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PatientController {
    private final SecurityService securityService;
    private final Map<UUID, Patient> patients;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public PatientController(SecurityService securityService) {
        this.securityService = securityService;
        this.patients = new ConcurrentHashMap<>();

        // Add some sample patients for testing
        addSamplePatients();
    }

    public void addListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    public void addPatient(Patient patient) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:create",
            Set.of(Permission.WRITE)
        );
        patients.put(patient.getId(), patient);
        notifyListeners();
    }

    public Optional<Patient> getPatient(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:read",
            Set.of(Permission.READ)
        );
        return Optional.ofNullable(patients.get(id));
    }

    public List<Patient> getAllPatients() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:read",
            Set.of(Permission.READ)
        );
        return new ArrayList<>(patients.values());
    }

    public void updatePatient(Patient patient) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:update",
            Set.of(Permission.WRITE)
        );

        if (!patients.containsKey(patient.getId())) {
            throw new IllegalArgumentException("Patient not found");
        }
        patients.put(patient.getId(), patient);
        notifyListeners();
    }

    public Map<String, UUID> getPatientDisplayMap() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:read",
            Set.of(Permission.READ)
        );

        Map<String, UUID> displayMap = new LinkedHashMap<>();
        getAllPatients().stream()
            .sorted(Comparator.comparing(Patient::getLastName)
                    .thenComparing(Patient::getFirstName))
            .forEach(p -> displayMap.put(
                String.format("%s, %s (ID: %s)",
                    p.getLastName(),
                    p.getFirstName(),
                    p.getId().toString().substring(0, 8)),
                p.getId()
            ));
        return displayMap;
    }

    private void addSamplePatients() {
        // Add some sample patients for testing
        try {
            Patient patient1 = new Patient.Builder()
                .withFirstName("John")
                .withLastName("Doe")
                .withDateOfBirth(java.time.LocalDate.of(1980, 1, 1))
                .withContactNumber("123-456-7890")
                .withEmail("john.doe@example.com")
                .withInsurance("POL-123456", "MediCare Plus")
                .build();
            patients.put(patient1.getId(), patient1);

            Patient patient2 = new Patient.Builder()
                .withFirstName("Jane")
                .withLastName("Smith")
                .withDateOfBirth(java.time.LocalDate.of(1990, 6, 15))
                .withContactNumber("987-654-3210")
                .withEmail("jane.smith@example.com")
                .withInsurance("POL-789012", "HealthFirst")
                .build();
            patients.put(patient2.getId(), patient2);

            Patient patient3 = new Patient.Builder()
                .withFirstName("Robert")
                .withLastName("Johnson")
                .withDateOfBirth(java.time.LocalDate.of(1975, 3, 21))
                .withContactNumber("555-123-4567")
                .withEmail("robert.j@example.com")
                .withInsurance("POL-345678", "Global Health")
                .build();
            patients.put(patient3.getId(), patient3);
        } catch (Exception e) {
            // Log error in real application
            e.printStackTrace();
        }
    }
}

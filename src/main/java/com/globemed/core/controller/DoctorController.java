package com.globemed.core.controller;

import com.globemed.core.model.Doctor;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import com.globemed.core.util.DataChangeListener;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DoctorController {
    private final SecurityService securityService;
    private final Map<UUID, Doctor> doctors;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public DoctorController(SecurityService securityService) {
        this.securityService = securityService;
        this.doctors = new ConcurrentHashMap<>();
        addSampleDoctors();
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

    public void addDoctor(Doctor doctor) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:create",
            Set.of(Permission.WRITE)
        );
        doctors.put(doctor.getId(), doctor);
        notifyListeners();
    }

    public void updateDoctor(Doctor doctor) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:update",
            Set.of(Permission.WRITE)
        );
        doctors.put(doctor.getId(), doctor);
        notifyListeners();
    }

    public Optional<Doctor> getDoctor(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );
        return Optional.ofNullable(doctors.get(id));
    }

    public List<Doctor> getAllDoctors() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );
        return new ArrayList<>(doctors.values());
    }

    public Map<String, UUID> getDoctorDisplayMap() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );

        return doctors.values().stream()
            .sorted(Comparator.comparing(Doctor::getLastName)
                    .thenComparing(Doctor::getFirstName))
            .collect(Collectors.toMap(
                Doctor::getDisplayName,
                Doctor::getId,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    private void addSampleDoctors() {
        try {
            Doctor doc1 = new Doctor.Builder()
                .withFirstName("John")
                .withLastName("Smith")
                .withSpecialty("General Medicine")
                .withContactNumber("555-0123")
                .withEmail("john.smith@globemed.com")
                .build();
            doctors.put(doc1.getId(), doc1);

            Doctor doc2 = new Doctor.Builder()
                .withFirstName("Sarah")
                .withLastName("Johnson")
                .withSpecialty("Cardiology")
                .withContactNumber("555-0124")
                .withEmail("sarah.johnson@globemed.com")
                .build();
            doctors.put(doc2.getId(), doc2);

            Doctor doc3 = new Doctor.Builder()
                .withFirstName("Michael")
                .withLastName("Brown")
                .withSpecialty("Pediatrics")
                .withContactNumber("555-0125")
                .withEmail("michael.brown@globemed.com")
                .build();
            doctors.put(doc3.getId(), doc3);

            Doctor doc4 = new Doctor.Builder()
                .withFirstName("Emily")
                .withLastName("Davis")
                .withSpecialty("Neurology")
                .withContactNumber("555-0126")
                .withEmail("emily.davis@globemed.com")
                .build();
            doctors.put(doc4.getId(), doc4);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize sample doctors", e);
        }
    }
}

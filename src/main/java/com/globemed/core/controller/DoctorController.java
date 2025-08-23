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
            // Add some sample doctors
            Set<String> weekdays = Set.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
            Set<String> fullWeek = Set.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

            Doctor doc1 = new Doctor.Builder()
                .withFirstName("John")
                .withLastName("Smith")
                .withSpecialization("Cardiology")
                .withContactNumber("123-555-0101")
                .withEmail("john.smith@globemed.com")
                .withWorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0))
                .withWorkingDays(weekdays)
                .build();
            doctors.put(doc1.getId(), doc1);

            Doctor doc2 = new Doctor.Builder()
                .withFirstName("Sarah")
                .withLastName("Johnson")
                .withSpecialization("Pediatrics")
                .withContactNumber("123-555-0102")
                .withEmail("sarah.johnson@globemed.com")
                .withWorkingHours(LocalTime.of(8, 0), LocalTime.of(16, 0))
                .withWorkingDays(weekdays)
                .build();
            doctors.put(doc2.getId(), doc2);

            Doctor doc3 = new Doctor.Builder()
                .withFirstName("Michael")
                .withLastName("Brown")
                .withSpecialization("Emergency Medicine")
                .withContactNumber("123-555-0103")
                .withEmail("michael.brown@globemed.com")
                .withWorkingHours(LocalTime.of(0, 0), LocalTime.of(23, 59))
                .withWorkingDays(fullWeek)
                .build();
            doctors.put(doc3.getId(), doc3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

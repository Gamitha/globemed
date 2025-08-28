package com.globemed.core.controller;

import com.globemed.core.model.Doctor;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import com.globemed.core.util.DataChangeListener;
import com.globemed.core.repository.DoctorRepository;
import com.globemed.core.util.Logger;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class DoctorController {
    private static final Logger log = Logger.getLogger(DoctorController.class);
    private final SecurityService securityService;
    private final DoctorRepository doctorRepository;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public DoctorController(SecurityService securityService) {
        this.securityService = securityService;
        this.doctorRepository = new DoctorRepository(securityService);

        // Use system context for initialization
        securityService.setSystemContext();
        try {
            addSampleDoctors();
        } finally {
            securityService.clearSystemContext();
        }
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
        try {
            doctorRepository.save(doctor);
            notifyListeners();
            log.info("Doctor added successfully: Dr. {} {}", doctor.getFirstName(), doctor.getLastName());
        } catch (Exception e) {
            log.error("Error adding doctor: {}", e.getMessage());
            throw e;
        }
    }

    public void updateDoctor(Doctor doctor) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:update",
            Set.of(Permission.WRITE)
        );
        try {
            doctorRepository.save(doctor);
            notifyListeners();
            log.info("Doctor updated successfully: Dr. {} {}", doctor.getFirstName(), doctor.getLastName());
        } catch (Exception e) {
            log.error("Error updating doctor: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<Doctor> getDoctor(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );
        try {
            return doctorRepository.findById(id.toString());
        } catch (Exception e) {
            log.error("Error retrieving doctor: {}", e.getMessage());
            throw e;
        }
    }

    public List<Doctor> getAllDoctors() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all doctors: {}", e.getMessage());
            throw e;
        }
    }

    public Map<String, UUID> getDoctorDisplayMap() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );

        Map<String, UUID> displayMap = new LinkedHashMap<>();
        getAllDoctors().forEach(doctor -> {
            displayMap.put(
                String.format("%s, %s (ID: %s)",
                    doctor.getLastName(),
                    doctor.getFirstName(),
                    doctor.getId().toString().substring(0, 8)),
                doctor.getId()
            );
        });
        return displayMap;
    }

    private void addSampleDoctors() {
        try {
            // Only add sample doctors if none exist
            if (getAllDoctors().isEmpty()) {
                Doctor doc1 = new Doctor.Builder()
                    .withFirstName("John")
                    .withLastName("Smith")
                    .withSpecialty("General Medicine")
                    .withContactNumber("555-0123")
                    .withEmail("john.smith@globemed.com")
                    .withWorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0))
                    .build();
                doctorRepository.save(doc1);

                Doctor doc2 = new Doctor.Builder()
                    .withFirstName("Sarah")
                    .withLastName("Johnson")
                    .withSpecialty("Cardiology")
                    .withContactNumber("555-0124")
                    .withEmail("sarah.johnson@globemed.com")
                    .withWorkingHours(LocalTime.of(8, 30), LocalTime.of(16, 30))
                    .build();
                doctorRepository.save(doc2);

                log.info("Sample doctors added successfully");
            }
        } catch (Exception e) {
            log.error("Error adding sample doctors: {}", e.getMessage());
        }
    }
}

package com.globemed.core.controller;

import com.globemed.core.model.Patient;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Doctor;
import com.globemed.core.security.Permission;
import com.globemed.core.security.Role;
import com.globemed.core.security.SecurityService;
import com.globemed.core.util.DataChangeListener;
import com.globemed.core.util.Logger;
import com.globemed.core.repository.PatientRepository;

import java.util.*;

/**
 * Controller for managing patient records and medical data.
 * Implements security checks and maintains data persistence using MapDB.
 */
public class PatientController {
    private static final Logger log = Logger.getLogger(PatientController.class);
    private final SecurityService securityService;
    private final PatientRepository patientRepository;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public PatientController(SecurityService securityService) {
        this.securityService = securityService;
        this.patientRepository = new PatientRepository(securityService);

        // Use system context for initialization
        securityService.setSystemContext();
        try {
            addSamplePatients();
        } finally {
            securityService.clearSystemContext();
        }
    }

    public String getCurrentUser() {
        return securityService.getCurrentUser();
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
        try {
            patientRepository.save(patient);
            notifyListeners();
            log.info("Patient added successfully: " + patient.getId());
        } catch (SecurityException e) {
            log.error("Security violation while adding patient: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error adding patient: " + e.getMessage());
            throw new RuntimeException("Failed to add patient", e);
        }
    }

    public void updatePatient(Patient patient) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:update",
            Set.of(Permission.WRITE)
        );

        if (!getPatient(patient.getId().toString()).isPresent()) {
            throw new IllegalArgumentException("Patient not found");
        }
        try {
            patientRepository.save(patient);
            notifyListeners();
            log.info("Patient updated successfully: {}", patient.getId());
        } catch (Exception e) {
            log.error("Error updating patient: {}", e.getMessage());
            throw new RuntimeException("Failed to update patient", e);
        }
    }

    public Optional<Patient> getPatient(String id) {
        try {
            return patientRepository.findById(id);
        } catch (SecurityException e) {
            log.error("Security violation while retrieving patient: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving patient: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve patient", e);
        }
    }

    public List<Patient> getAllPatients() {
        try {
            return patientRepository.findAll();
        } catch (SecurityException e) {
            log.error("Security violation while retrieving patients: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving patients: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve patients", e);
        }
    }

    public void deletePatient(String id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:delete",
            Set.of(Permission.WRITE)
        );
        try {
            patientRepository.delete(id);
            notifyListeners();
            log.info("Patient deleted successfully: " + id);
        } catch (SecurityException e) {
            log.error("Security violation while deleting patient: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting patient: " + e.getMessage());
            throw new RuntimeException("Failed to delete patient", e);
        }
    }

    private void addSamplePatients() {
        try {
            // Create doctors first to get their UUIDs
            Doctor doctor1 = new Doctor.Builder()
                .withFirstName("John")
                .withLastName("Smith")
                .withSpecialty("General Medicine")
                .build();

            Doctor doctor2 = new Doctor.Builder()
                .withFirstName("Sarah")
                .withLastName("Johnson")
                .withSpecialty("Cardiology")
                .build();

            Doctor doctor3 = new Doctor.Builder()
                .withFirstName("Michael")
                .withLastName("Brown")
                .withSpecialty("Pediatrics")
                .build();

            // Add sample patient 1 with medical records
            Patient patient1 = new Patient.Builder()
                .withFirstName("John")
                .withLastName("Doe")
                .withDateOfBirth(java.time.LocalDate.of(1980, 1, 1))
                .withContactNumber("123-456-7890")
                .withEmail("john.doe@example.com")
                .withInsurance("POL-123456", "MediCare Plus")
                .build();
            patientRepository.save(patient1);

            // Add medical records for patient 1
            MedicalRecord record1 = new MedicalRecord.Builder()
                .withPatientId(patient1.getId())
                .withDiagnosis("Hypertension")
                .withTreatment("Prescribed ACE inhibitors")
                .withTestsPerformed("Blood pressure monitoring, ECG")
                .withTestResults("BP: 140/90, ECG: Normal sinus rhythm")
                .withRecommendations("Regular exercise, low-sodium diet")
                .withNotes("Follow-up in 3 months")
                .withDoctorId(doctor1.getId())
                .build();
            patient1.addMedicalRecord(record1);

            // Add sample patient 2 with medical records
            Patient patient2 = new Patient.Builder()
                .withFirstName("Jane")
                .withLastName("Smith")
                .withDateOfBirth(java.time.LocalDate.of(1990, 6, 15))
                .withContactNumber("987-654-3210")
                .withEmail("jane.smith@example.com")
                .withInsurance("POL-789012", "HealthFirst")
                .build();
            patientRepository.save(patient2);

            // Add medical records for patient 2
            MedicalRecord record2 = new MedicalRecord.Builder()
                .withPatientId(patient2.getId())
                .withDiagnosis("Type 2 Diabetes")
                .withTreatment("Metformin 500mg twice daily")
                .withTestsPerformed("HbA1c, Blood glucose")
                .withTestResults("HbA1c: 7.2%, Glucose: 135 mg/dL")
                .withRecommendations("Regular blood sugar monitoring")
                .withNotes("Diet and exercise plan discussed")
                .withDoctorId(doctor2.getId())
                .build();
            patient2.addMedicalRecord(record2);

            // Add sample patient 3 with medical records
            Patient patient3 = new Patient.Builder()
                .withFirstName("Robert")
                .withLastName("Johnson")
                .withDateOfBirth(java.time.LocalDate.of(1975, 3, 21))
                .withContactNumber("555-123-4567")
                .withEmail("robert.j@example.com")
                .withInsurance("POL-345678", "Global Health")
                .build();
            patientRepository.save(patient3);

            // Add medical records for patient 3
            MedicalRecord record3 = new MedicalRecord.Builder()
                .withPatientId(patient3.getId())
                .withDiagnosis("Lower back pain")
                .withTreatment("Physical therapy, NSAIDs")
                .withTestsPerformed("X-ray, MRI of lumbar spine")
                .withTestResults("Mild disc degeneration at L4-L5")
                .withRecommendations("Continue PT exercises")
                .withNotes("Showing improvement with current treatment")
                .withDoctorId(doctor3.getId())
                .build();
            patient3.addMedicalRecord(record3);

        } catch (Exception e) {
            log.error("Error adding sample patients", e);
        }
    }

    public boolean addMedicalRecord(UUID patientId, MedicalRecord record) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "medical:create",
            Set.of(Permission.WRITE)
        );

        Optional<Patient> patient = getPatient(patientId.toString());
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }

        try {
            // If record already has a doctorId (set by admin via UI), use it
            UUID doctorId = record.getDoctorId();

            // If no doctorId set, determine it based on user role
            if (doctorId == null) {
                if (securityService.hasRole(Role.ADMIN)) {
                    throw new IllegalStateException("Admin users must select a doctor when creating medical records");
                } else {
                    // Current user must be a doctor, get their ID
                    String currentUser = securityService.getCurrentUser();
                    doctorId = getAllDoctors().stream()
                        .filter(d -> d.getId().toString().startsWith(currentUser))
                        .findFirst()
                        .map(Doctor::getId)
                        .orElseThrow(() -> new IllegalStateException("Current user is not a valid doctor"));
                }
            }

            // Create a new record with all fields, ensuring doctorId is set
            MedicalRecord finalRecord = new MedicalRecord.Builder()
                .withPatientId(patientId)
                .withDiagnosis(record.getDiagnosis())
                .withTreatment(record.getTreatment())
                .withTestsPerformed(record.getTestsPerformed())
                .withTestResults(record.getTestResults())
                .withRecommendations(record.getRecommendations())
                .withNotes(record.getNotes())
                .withDoctorId(doctorId)
                .build();

            Patient updatedPatient = patient.get();
            updatedPatient.addMedicalRecord(finalRecord);
            patientRepository.save(updatedPatient);
            log.info("Added medical record for patient: {}", patientId);
            notifyListeners();

            return true;
        } catch (Exception e) {
            log.error("Error adding medical record: {}", e.getMessage());
            return false;
        }
    }

    public boolean hasRole(Role role) {
        if (role == null) {
            return false;
        }
        return securityService.hasRole(role);
    }

    public Map<String, UUID> getDoctorDisplayMap() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "doctor:read",
            Set.of(Permission.READ)
        );

        Map<String, UUID> displayMap = new LinkedHashMap<>();
        for (Doctor doctor : getAllDoctors()) {
            displayMap.put(
                String.format("%s, %s (ID: %s)",
                    doctor.getLastName(),
                    doctor.getFirstName(),
                    doctor.getId().toString().substring(0, 8)),
                doctor.getId()
            );
        }
        return displayMap;
    }

    public Map<String, UUID> getPatientDisplayMap() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "patient:read",
            Set.of(Permission.READ)
        );

        Map<String, UUID> displayMap = new LinkedHashMap<>();
        getAllPatients().forEach(patient -> {
            displayMap.put(
                String.format("%s, %s (ID: %s)",
                    patient.getLastName(),
                    patient.getFirstName(),
                    patient.getId().toString().substring(0, 8)),
                patient.getId()
            );
        });
        return displayMap;
    }

    private List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();

        // Add demo doctors with auto-generated UUIDs
        doctors.add(new Doctor.Builder()
            .withFirstName("John")
            .withLastName("Smith")
            .withSpecialty("General Medicine")
            .withContactNumber("555-0123")
            .withEmail("john.smith@globemed.com")
            .build());

        doctors.add(new Doctor.Builder()
            .withFirstName("Sarah")
            .withLastName("Johnson")
            .withSpecialty("Cardiology")
            .withContactNumber("555-0124")
            .withEmail("sarah.johnson@globemed.com")
            .build());

        doctors.add(new Doctor.Builder()
            .withFirstName("Michael")
            .withLastName("Brown")
            .withSpecialty("Pediatrics")
            .withContactNumber("555-0125")
            .withEmail("michael.brown@globemed.com")
            .build());

        doctors.add(new Doctor.Builder()
            .withFirstName("Emily")
            .withLastName("Davis")
            .withSpecialty("Neurology")
            .withContactNumber("555-0126")
            .withEmail("emily.davis@globemed.com")
            .build());

        return doctors;
    }
}

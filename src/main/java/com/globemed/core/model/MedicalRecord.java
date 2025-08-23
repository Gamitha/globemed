package com.globemed.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class MedicalRecord {
    private UUID id;
    private UUID patientId;
    private LocalDateTime dateTime;
    private String diagnosis;
    private String treatment;
    private String notes;
    private String doctorId;
    private String testsPerformed;
    private String testResults;
    private String recommendations;

    public MedicalRecord() {
        this.id = UUID.randomUUID();
        this.dateTime = LocalDateTime.now();
    }

    // Builder Pattern
    public static class Builder {
        private MedicalRecord record;

        public Builder() {
            record = new MedicalRecord();
        }

        public Builder withPatientId(UUID patientId) {
            record.patientId = patientId;
            return this;
        }

        public Builder withDiagnosis(String diagnosis) {
            record.diagnosis = diagnosis;
            return this;
        }

        public Builder withTreatment(String treatment) {
            record.treatment = treatment;
            return this;
        }

        public Builder withNotes(String notes) {
            record.notes = notes;
            return this;
        }

        public Builder withDoctorId(String doctorId) {
            record.doctorId = doctorId;
            return this;
        }

        public Builder withTestsPerformed(String testsPerformed) {
            record.testsPerformed = testsPerformed;
            return this;
        }

        public Builder withTestResults(String testResults) {
            record.testResults = testResults;
            return this;
        }

        public Builder withRecommendations(String recommendations) {
            record.recommendations = recommendations;
            return this;
        }

        public MedicalRecord build() {
            // Validate required fields
            if (record.patientId == null) {
                throw new IllegalStateException("Patient ID is required");
            }
            if (record.doctorId == null) {
                throw new IllegalStateException("Doctor ID is required");
            }
            if (record.diagnosis == null || record.diagnosis.trim().isEmpty()) {
                throw new IllegalStateException("Diagnosis is required");
            }
            return record;
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public String getNotes() { return notes; }
    public String getDoctorId() { return doctorId; }
    public String getTestsPerformed() { return testsPerformed != null ? testsPerformed : "None"; }
    public String getTestResults() { return testResults != null ? testResults : "No results available"; }
    public String getRecommendations() { return recommendations != null ? recommendations : "No recommendations"; }
}

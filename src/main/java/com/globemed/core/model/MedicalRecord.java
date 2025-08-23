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

        public MedicalRecord build() {
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
}

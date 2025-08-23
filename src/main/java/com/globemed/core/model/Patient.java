package com.globemed.core.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Patient {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final String contactNumber;
    private final String email;
    private final String insurancePolicy;
    private final String insuranceProvider;
    private List<MedicalRecord> medicalRecords;

    private Patient(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.dateOfBirth = builder.dateOfBirth;
        this.contactNumber = builder.contactNumber;
        this.email = builder.email;
        this.insurancePolicy = builder.insurancePolicy;
        this.insuranceProvider = builder.insuranceProvider;
        this.medicalRecords = new ArrayList<>();
    }

    // Builder pattern implementation
    public static class Builder {
        private UUID id;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String contactNumber;
        private String email;
        private String insurancePolicy;
        private String insuranceProvider;

        public Builder() {
            this.id = UUID.randomUUID();
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder withContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withInsurance(String policy, String provider) {
            this.insurancePolicy = policy;
            this.insuranceProvider = provider;
            return this;
        }

        public Patient build() {
            if (firstName == null || lastName == null || dateOfBirth == null) {
                throw new IllegalStateException("Required fields must be set");
            }
            return new Patient(this);
        }
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getInsurancePolicy() {
        return insurancePolicy;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return new ArrayList<>(medicalRecords);
    }

    public void addMedicalRecord(MedicalRecord record) {
        this.medicalRecords.add(record);
    }
}

package com.globemed.core.model;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a doctor in the healthcare system.
 * Uses Builder pattern for flexible object construction.
 */
public class Doctor {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String specialty;
    private final String contactNumber;
    private final String email;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Set<String> workingDays;

    private Doctor(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.specialty = builder.specialty;
        this.contactNumber = builder.contactNumber;
        this.email = builder.email;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.workingDays = builder.workingDays;
    }

    // Builder pattern implementation
    public static class Builder {
        private UUID id;
        private String firstName;
        private String lastName;
        private String specialty;
        private String contactNumber;
        private String email;
        private LocalTime startTime;
        private LocalTime endTime;
        private Set<String> workingDays;

        public Builder() {
            this.id = UUID.randomUUID();
            this.workingDays = new HashSet<>();
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

        public Builder withSpecialty(String specialty) {
            this.specialty = specialty;
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

        public Builder withWorkingHours(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            return this;
        }

        public Builder withWorkingDays(Set<String> workingDays) {
            this.workingDays = workingDays;
            return this;
        }

        public Doctor build() {
            if (id == null || firstName == null || lastName == null) {
                throw new IllegalStateException("Required fields must be set: id, firstName, lastName");
            }
            return new Doctor(this);
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getSpecialization() {  // Alias for getSpecialty()
        return specialty;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Set<String> getWorkingDays() {
        return workingDays;
    }

    public String getDisplayName() {
        return String.format("%s, %s", lastName, firstName);
    }
}

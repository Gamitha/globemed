package com.globemed.core.model;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Doctor {
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String contactNumber;
    private String email;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<String> workingDays;

    private Doctor() {
        this.id = UUID.randomUUID();
        this.workingDays = new HashSet<>();
    }

    // Builder Pattern implementation
    public static class Builder {
        private Doctor doctor;

        public Builder() {
            doctor = new Doctor();
        }

        public Builder withFirstName(String firstName) {
            doctor.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            doctor.lastName = lastName;
            return this;
        }

        public Builder withSpecialization(String specialization) {
            doctor.specialization = specialization;
            return this;
        }

        public Builder withContactNumber(String contactNumber) {
            doctor.contactNumber = contactNumber;
            return this;
        }

        public Builder withEmail(String email) {
            doctor.email = email;
            return this;
        }

        public Builder withWorkingHours(LocalTime start, LocalTime end) {
            doctor.startTime = start;
            doctor.endTime = end;
            return this;
        }

        public Builder withWorkingDays(Set<String> days) {
            doctor.workingDays = new HashSet<>(days);
            return this;
        }

        public Doctor build() {
            if (doctor.firstName == null || doctor.lastName == null || doctor.specialization == null) {
                throw new IllegalStateException("First name, last name, and specialization are required");
            }
            return doctor;
        }
    }

    // Getters
    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpecialization() { return specialization; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public Set<String> getWorkingDays() { return new HashSet<>(workingDays); }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        return String.format("%s - Dr. %s %s (%s)",
            id.toString().substring(0, 8),
            firstName,
            lastName,
            specialization);
    }
}

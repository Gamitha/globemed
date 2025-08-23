package com.globemed.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bill {
    private final UUID id;
    private final UUID patientId;
    private final LocalDateTime dateTime;
    private final List<BillItem> items;
    private final BigDecimal totalAmount;
    private BillStatus status;
    private String insurancePolicyNumber;
    private String insuranceProvider;
    private BigDecimal coveredAmount;
    private String notes;

    private Bill(Builder builder) {
        this.id = UUID.randomUUID();
        this.patientId = builder.patientId;
        this.dateTime = LocalDateTime.now();
        this.items = new ArrayList<>(builder.items);
        this.totalAmount = calculateTotal();
        this.status = BillStatus.PENDING;
        this.insurancePolicyNumber = builder.insurancePolicyNumber;
        this.insuranceProvider = builder.insuranceProvider;
        this.coveredAmount = BigDecimal.ZERO;
        this.notes = builder.notes;
    }

    private BigDecimal calculateTotal() {
        return items.stream()
                .map(BillItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Builder Pattern
    public static class Builder {
        private UUID patientId;
        private List<BillItem> items = new ArrayList<>();
        private String insurancePolicyNumber;
        private String insuranceProvider;
        private String notes;

        public Builder withPatientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder addItem(BillItem item) {
            this.items.add(item);
            return this;
        }

        public Builder withInsurancePolicy(String policyNumber, String provider) {
            this.insurancePolicyNumber = policyNumber;
            this.insuranceProvider = provider;
            return this;
        }

        public Builder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Bill build() {
            if (patientId == null) {
                throw new IllegalStateException("Patient ID is required");
            }
            if (items.isEmpty()) {
                throw new IllegalStateException("Bill must have at least one item");
            }
            return new Bill(this);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<BillItem> getItems() { return new ArrayList<>(items); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BillStatus getStatus() { return status; }
    public String getInsurancePolicyNumber() { return insurancePolicyNumber; }
    public String getInsuranceProvider() { return insuranceProvider; }
    public BigDecimal getCoveredAmount() { return coveredAmount; }
    public String getNotes() { return notes; }

    // Setters for mutable properties
    public void setStatus(BillStatus status) { this.status = status; }
    public void setCoveredAmount(BigDecimal amount) { this.coveredAmount = amount; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(coveredAmount);
    }
}

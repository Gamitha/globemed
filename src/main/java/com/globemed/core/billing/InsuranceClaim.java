package com.globemed.core.billing;

import com.globemed.core.model.Bill;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class InsuranceClaim implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Bill bill;
    private final LocalDateTime submissionDate;
    private ClaimStatus status;
    private String notes;
    private BigDecimal amount;
    private BigDecimal approvedAmount;
    private String insuranceProvider;
    private String policyNumber;

    private InsuranceClaim(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.bill = builder.bill;
        this.submissionDate = builder.submissionDate != null ? builder.submissionDate : LocalDateTime.now();
        this.status = ClaimStatus.SUBMITTED;
        this.notes = "";
        this.amount = builder.amount;
        this.approvedAmount = null;
        this.insuranceProvider = builder.insuranceProvider;
        this.policyNumber = builder.policyNumber;
    }

    // Builder Pattern
    public static class Builder {
        private UUID id;
        private Bill bill;
        private LocalDateTime submissionDate;
        private BigDecimal amount;
        private String insuranceProvider;
        private String policyNumber;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withBill(Bill bill) {
            this.bill = bill;
            return this;
        }

        public Builder withSubmissionDate(LocalDateTime date) {
            this.submissionDate = date;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withInsuranceProvider(String provider) {
            this.insuranceProvider = provider;
            return this;
        }

        public Builder withPolicyNumber(String policyNumber) {
            this.policyNumber = policyNumber;
            return this;
        }

        public InsuranceClaim build() {
            if (bill == null && (amount == null || insuranceProvider == null || policyNumber == null)) {
                throw new IllegalStateException("Either Bill or complete claim details are required");
            }

            // If bill is provided, use its details
            if (bill != null) {
                this.amount = bill.getTotalAmount();
                this.insuranceProvider = bill.getInsuranceProvider();
                this.policyNumber = bill.getInsurancePolicyNumber();
            }

            return new InsuranceClaim(this);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public Bill getBill() { return bill; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public ClaimStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public String getInsuranceProvider() { return insuranceProvider; }
    public String getPolicyNumber() { return policyNumber; }

    // Setters
    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    @Override
    public String toString() {
        return String.format("InsuranceClaim{id=%s, amount=%.2f, status=%s}",
            id, amount, status);
    }
}

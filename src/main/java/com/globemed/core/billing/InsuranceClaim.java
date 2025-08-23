package com.globemed.core.billing;

import com.globemed.core.model.Bill;
import java.time.LocalDateTime;
import java.util.UUID;

public class InsuranceClaim {
    private final UUID id;
    private final Bill bill;
    private final LocalDateTime submissionDate;
    private ClaimStatus status;
    private String notes;

    private InsuranceClaim(Builder builder) {
        this.id = UUID.randomUUID();
        this.bill = builder.bill;
        this.submissionDate = LocalDateTime.now();
        this.status = ClaimStatus.SUBMITTED;
        this.notes = "";
    }

    // Builder Pattern
    public static class Builder {
        private Bill bill;

        public Builder withBill(Bill bill) {
            this.bill = bill;
            return this;
        }

        public InsuranceClaim build() {
            if (bill == null) {
                throw new IllegalStateException("Bill is required");
            }
            if (bill.getInsurancePolicyNumber() == null ||
                bill.getInsuranceProvider() == null) {
                throw new IllegalStateException("Insurance details are required");
            }
            return new InsuranceClaim(this);
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public Bill getBill() { return bill; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public ClaimStatus getStatus() { return status; }
    public String getNotes() { return notes; }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

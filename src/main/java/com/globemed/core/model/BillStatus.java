package com.globemed.core.model;

public enum BillStatus {
    DRAFT("Draft"),
    PENDING("Pending"),
    PAID("Paid"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled"),
    ERROR("Error"),
    INSURANCE_SUBMITTED("Insurance Submitted"),
    INSURANCE_PENDING("Insurance Review Pending"),
    INSURANCE_APPROVED("Insurance Approved"),
    INSURANCE_REJECTED("Insurance Rejected");

    private final String displayName;

    BillStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines if an insurance claim can be submitted for a bill in this status
     * @return true if a claim can be submitted, false otherwise
     */
    public boolean canSubmitClaim() {
        switch (this) {
            case DRAFT:
            case PENDING:
            case ERROR:
                return true;
            case INSURANCE_REJECTED:
                // Allow resubmission of rejected claims
                return true;
            default:
                return false;
        }
    }
}

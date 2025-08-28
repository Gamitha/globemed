package com.globemed.core.billing;

public enum ClaimStatus {
    SUBMITTED("Submitted"),
    VALIDATING("Validating"),
    VALIDATED("Validated"),
    VERIFYING_COVERAGE("Verifying Coverage"),
    COVERAGE_VERIFIED("Coverage Verified"),
    CALCULATING_COVERAGE("Calculating Coverage"),
    PENDING_REVIEW("Pending Review"),
    AMOUNT_APPROVED("Amount Approved"),
    PROCESSING_PAYMENT("Processing Payment"),
    PAYMENT_FAILED("Payment Failed"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SETTLED("Settled");

    private final String displayName;

    ClaimStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

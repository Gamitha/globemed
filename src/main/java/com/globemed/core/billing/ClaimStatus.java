package com.globemed.core.billing;

public enum ClaimStatus {
    SUBMITTED("Submitted"),
    VALIDATING("Validating"),
    VERIFYING_COVERAGE("Verifying Coverage"),
    CALCULATING_COVERAGE("Calculating Coverage"),
    PROCESSING_PAYMENT("Processing Payment"),
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

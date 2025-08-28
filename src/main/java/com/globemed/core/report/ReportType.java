package com.globemed.core.report;

public enum ReportType {
    PATIENT("Patient Report"),
    FINANCIAL("Financial Report"),
    DIAGNOSTIC("Diagnostic Report"),
    BILLING("Billing Report"),
    TREATMENT_SUMMARY("Treatment Summary");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

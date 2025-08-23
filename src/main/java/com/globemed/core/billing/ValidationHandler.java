package com.globemed.core.billing;

public class ValidationHandler extends ClaimHandler {
    @Override
    public void processRequest(InsuranceClaim claim) {
        // Validate policy number and basic information
        if (claim.getBill().getInsurancePolicyNumber() == null ||
            claim.getBill().getInsurancePolicyNumber().trim().isEmpty()) {
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Invalid insurance policy number");
            return;
        }

        if (claim.getBill().getTotalAmount().doubleValue() <= 0) {
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Invalid bill amount");
            return;
        }

        claim.setStatus(ClaimStatus.VALIDATING);
        if (nextHandler != null) {
            nextHandler.processRequest(claim);
        }
    }
}

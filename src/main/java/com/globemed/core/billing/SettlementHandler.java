package com.globemed.core.billing;

public class SettlementHandler extends ClaimHandler {
    @Override
    public void processRequest(InsuranceClaim claim) {
        // Process payment and update bill status
        claim.setStatus(ClaimStatus.PROCESSING_PAYMENT);

        // Update bill status
        claim.getBill().setStatus(com.globemed.core.model.BillStatus.INSURANCE_APPROVED);

        // In real system, initiate payment process here

        claim.setStatus(ClaimStatus.SETTLED);
    }
}

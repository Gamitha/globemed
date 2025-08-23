package com.globemed.core.billing;

import java.math.BigDecimal;

public class AmountApprovalHandler extends ClaimHandler {
    @Override
    public void processRequest(InsuranceClaim claim) {
        // Calculate covered amount based on policy
        claim.setStatus(ClaimStatus.CALCULATING_COVERAGE);

        // Demo implementation - cover 80% of the total
        double coveredAmount = claim.getBill().getTotalAmount().doubleValue() * 0.8;
        claim.getBill().setCoveredAmount(BigDecimal.valueOf(coveredAmount));

        claim.setStatus(ClaimStatus.APPROVED);
        if (nextHandler != null) {
            nextHandler.processRequest(claim);
        }
    }
}

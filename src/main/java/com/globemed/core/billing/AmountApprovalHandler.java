package com.globemed.core.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Handles amount validation and approval in the claim processing chain.
 * Determines if claims can be auto-approved or need manual review based on amount thresholds.
 */
public class AmountApprovalHandler extends ClaimHandler {
    private static final Logger logger = LoggerFactory.getLogger(AmountApprovalHandler.class);
    private static final BigDecimal AUTO_APPROVAL_THRESHOLD = new BigDecimal("1000.00");
    private static final BigDecimal MAX_CLAIM_AMOUNT = new BigDecimal("100000.00");
    private static final BigDecimal MIN_CLAIM_AMOUNT = new BigDecimal("10.00");

    @Override
    protected void processInternal(InsuranceClaim claim) {
        logger.info("Processing amount approval for claim: {}", claim.getId());

        // Verify claim is in correct state
        if (claim.getStatus() != ClaimStatus.COVERAGE_VERIFIED) {
            logger.error("Claim {} has incorrect status for amount approval: {}", claim.getId(), claim.getStatus());
            throw new ClaimProcessingException("Claim must be in COVERAGE_VERIFIED status for amount approval");
        }

        validateAmount(claim);
        determineApprovalPath(claim);
    }

    private void validateAmount(InsuranceClaim claim) {
        if (claim.getAmount() == null) {
            logger.error("Claim {} is missing amount", claim.getId());
            throw new ClaimProcessingException("Missing claim amount");
        }

        BigDecimal amount = claim.getAmount().setScale(2, RoundingMode.HALF_UP);

        // Check minimum amount
        if (amount.compareTo(MIN_CLAIM_AMOUNT) < 0) {
            logger.warn("Claim {} amount {} is below minimum threshold", claim.getId(), amount);
            claim.setNotes("Claim amount is below minimum threshold of $" + MIN_CLAIM_AMOUNT);
            throw new ClaimProcessingException("Claim amount is below minimum threshold");
        }

        // Check maximum amount
        if (amount.compareTo(MAX_CLAIM_AMOUNT) > 0) {
            logger.warn("Claim {} amount {} exceeds maximum threshold", claim.getId(), amount);
            claim.setNotes("Claim amount exceeds maximum threshold of $" + MAX_CLAIM_AMOUNT);
            throw new ClaimProcessingException("Claim amount exceeds maximum threshold");
        }
    }

    private void determineApprovalPath(InsuranceClaim claim) {
        BigDecimal amount = claim.getAmount();

        // Check auto-approval threshold
        if (amount.compareTo(AUTO_APPROVAL_THRESHOLD) > 0) {
            logger.info("Claim {} amount {} exceeds auto-approval threshold, requiring manual review",
                claim.getId(), amount);
            claim.setStatus(ClaimStatus.PENDING_REVIEW);
            claim.setNotes("Amount exceeds auto-approval threshold of $" +
                AUTO_APPROVAL_THRESHOLD + ", requires manual review");
            return;
        }

        // Auto-approve claims within threshold
        logger.info("Auto-approving claim {} with amount {}", claim.getId(), amount);
        claim.setStatus(ClaimStatus.AMOUNT_APPROVED);
        claim.setNotes("Amount auto-approved within threshold of $" + AUTO_APPROVAL_THRESHOLD);

        // For demo purposes, set approved amount to 80% of claim amount
        BigDecimal approvedAmount = amount.multiply(new BigDecimal("0.80"))
            .setScale(2, RoundingMode.HALF_UP);
        claim.setApprovedAmount(approvedAmount);

        logger.debug("Set approved amount for claim {} to {}", claim.getId(), approvedAmount);
    }
}

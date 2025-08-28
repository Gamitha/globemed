package com.globemed.core.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Handles the final settlement step in the insurance claim processing chain.
 * Calculates coverage amount and initiates payment processing.
 */
public class SettlementHandler extends ClaimHandler {
    private static final Logger logger = LoggerFactory.getLogger(SettlementHandler.class);
    private static final BigDecimal COVERAGE_PERCENTAGE = new BigDecimal("0.80"); // 80% coverage
    private static final BigDecimal MAX_COVERAGE_AMOUNT = new BigDecimal("1000000.00"); // $1M maximum coverage

    @Override
    public void processInternal(InsuranceClaim claim) {
        logger.info("Processing settlement for claim: {}", claim.getId());

        // Validate claim status
        if (claim.getStatus() != ClaimStatus.AMOUNT_APPROVED) {
            logger.warn("Claim {} has incorrect status for settlement: {}", claim.getId(), claim.getStatus());
            throw new ClaimProcessingException("Claim must be in AMOUNT_APPROVED status for settlement");
        }

        try {
            // Calculate covered amount with maximum limit check
            BigDecimal approvedAmount = claim.getAmount().multiply(COVERAGE_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);

            // Apply maximum coverage limit
            if (approvedAmount.compareTo(MAX_COVERAGE_AMOUNT) > 0) {
                logger.warn("Claim {} exceeds maximum coverage limit. Adjusting to maximum.", claim.getId());
                approvedAmount = MAX_COVERAGE_AMOUNT;
            }

            claim.setApprovedAmount(approvedAmount);
            logger.debug("Calculated approved amount for claim {}: {}", claim.getId(), approvedAmount);

            // Initiate payment processing
            boolean paymentInitiated = initiatePayment(claim);

            if (!paymentInitiated) {
                logger.error("Failed to initiate payment for claim: {}", claim.getId());
                claim.setStatus(ClaimStatus.PAYMENT_FAILED);
                claim.setNotes("Failed to initiate payment");
                throw new ClaimProcessingException("Payment initiation failed for claim: " + claim.getId());
            }

            // Set final success status
            claim.setStatus(ClaimStatus.APPROVED);
            claim.setNotes("Payment successfully initiated for amount: " + approvedAmount);
            logger.info("Successfully processed settlement for claim: {}", claim.getId());

        } catch (ArithmeticException e) {
            logger.error("Arithmetic error processing claim {}: {}", claim.getId(), e.getMessage());
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Error calculating settlement amount");
            throw new ClaimProcessingException("Error calculating settlement amount", e);
        } catch (Exception e) {
            logger.error("Unexpected error processing claim {}: {}", claim.getId(), e.getMessage());
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Unexpected error during settlement");
            throw new ClaimProcessingException("Unexpected error during settlement", e);
        }
    }

    /**
     * Initiates the payment process for an approved claim.
     * In a real implementation, this would integrate with a payment processing system.
     *
     * @param claim The approved insurance claim to process payment for
     * @return true if payment initiation was successful, false otherwise
     */
    private boolean initiatePayment(InsuranceClaim claim) {
        try {
            // Simulated integration with payment processing system
            // In a real implementation, this would:
            // 1. Connect to payment gateway
            // 2. Submit payment request
            // 3. Handle response and verification

            logger.info("Initiating payment for claim {}, amount: {}", claim.getId(), claim.getApprovedAmount());

            // Simulate payment processing delay
            Thread.sleep(1000);

            // For demo purposes, fail payments over $500,000
            if (claim.getApprovedAmount().compareTo(new BigDecimal("500000.00")) > 0) {
                logger.warn("Payment failed for claim {} due to amount exceeding processor limit", claim.getId());
                return false;
            }

            return true;
        } catch (InterruptedException e) {
            logger.error("Payment processing interrupted for claim {}: {}", claim.getId(), e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
            return false;
        } catch (Exception e) {
            logger.error("Error processing payment for claim {}: {}", claim.getId(), e.getMessage());
            return false;
        }
    }
}

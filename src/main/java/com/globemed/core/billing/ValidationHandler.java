package com.globemed.core.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

/**
 * First handler in the claim processing chain. Validates basic claim information
 * before passing it to more complex processing steps.
 */
public class ValidationHandler extends ClaimHandler {
    private static final Logger logger = LoggerFactory.getLogger(ValidationHandler.class);

    @Override
    public void processInternal(InsuranceClaim claim) {
        claim.setStatus(ClaimStatus.VALIDATING);
        logger.info("Starting validation for claim: {}", claim.getId());

        try {
            validateClaimId(claim);
            validateBill(claim);
            validateAmount(claim);
            validatePolicyDetails(claim);
            validateDates(claim);

            // If all validations pass, set status to validated
            claim.setStatus(ClaimStatus.VALIDATED);
            claim.setNotes("Claim validation successful");
            logger.info("Claim {} successfully validated", claim.getId());

        } catch (ClaimProcessingException e) {
            // Let the base class handle the exception and status updates
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during claim validation: {}", e.getMessage());
            claim.setNotes("Internal validation error: " + e.getMessage());
            throw new ClaimProcessingException("Unexpected validation error", e);
        }
    }

    private void validateClaimId(InsuranceClaim claim) {
        if (claim.getId() == null) {
            logger.error("Claim is missing ID");
            throw new ClaimProcessingException("Missing claim ID");
        }
    }

    private void validateBill(InsuranceClaim claim) {
        if (claim.getBill() == null) {
            logger.error("Claim {} is missing bill information", claim.getId());
            throw new ClaimProcessingException("Missing bill information");
        }

        // Validate bill details
        if (claim.getBill().getId() == null ||
            claim.getBill().getPatientId() == null ||
            claim.getBill().getItems().isEmpty()) {
            logger.error("Claim {} has invalid bill details", claim.getId());
            throw new ClaimProcessingException("Invalid bill details - missing required information");
        }
    }

    private void validateAmount(InsuranceClaim claim) {
        if (claim.getAmount() == null || claim.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Claim {} has invalid amount: {}", claim.getId(), claim.getAmount());
            throw new ClaimProcessingException("Invalid claim amount");
        }

        // Validate amount matches bill total
        if (claim.getAmount().compareTo(claim.getBill().getTotalAmount()) != 0) {
            logger.error("Claim {} amount does not match bill total", claim.getId());
            throw new ClaimProcessingException("Claim amount does not match bill total");
        }
    }

    private void validatePolicyDetails(InsuranceClaim claim) {
        if (claim.getPolicyNumber() == null || claim.getPolicyNumber().trim().isEmpty()) {
            logger.error("Claim {} is missing policy number", claim.getId());
            throw new ClaimProcessingException("Missing policy number");
        }

        if (claim.getInsuranceProvider() == null || claim.getInsuranceProvider().trim().isEmpty()) {
            logger.error("Claim {} is missing insurance provider", claim.getId());
            throw new ClaimProcessingException("Missing insurance provider");
        }
    }

    private void validateDates(InsuranceClaim claim) {
        if (claim.getBill().getDateCreated() == null) {
            logger.error("Claim {} is missing bill date", claim.getId());
            throw new ClaimProcessingException("Missing bill date");
        }
    }
}

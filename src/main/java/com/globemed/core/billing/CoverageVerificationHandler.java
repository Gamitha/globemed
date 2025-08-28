package com.globemed.core.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * Handles insurance coverage verification in the claim processing chain.
 * Makes external API calls to verify insurance coverage and policy details.
 */
public class CoverageVerificationHandler extends ClaimHandler {
    private static final Logger logger = LoggerFactory.getLogger(CoverageVerificationHandler.class);
    private static final int VERIFICATION_TIMEOUT_SECONDS = 30;
    private final ExecutorService executorService;

    public CoverageVerificationHandler() {
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    protected void processInternal(InsuranceClaim claim) {
        logger.info("Starting coverage verification for claim: {}", claim.getId());

        // Input validation
        if (claim.getBill() == null) {
            logger.error("Claim {} is missing bill information", claim.getId());
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Missing bill information");
            throw new ClaimProcessingException("Missing bill information");
        }

        if (claim.getInsuranceProvider() == null) {
            logger.error("Claim {} is missing insurance provider information", claim.getId());
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Missing insurance provider information");
            throw new ClaimProcessingException("Missing insurance provider information");
        }

        if (claim.getPolicyNumber() == null || claim.getPolicyNumber().trim().isEmpty()) {
            logger.error("Claim {} is missing policy number", claim.getId());
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Missing or invalid policy number");
            throw new ClaimProcessingException("Missing or invalid policy number");
        }

        try {
            claim.setStatus(ClaimStatus.VERIFYING_COVERAGE);
            logger.debug("Attempting to verify coverage for claim {}", claim.getId());

            Future<Boolean> future = executorService.submit(() -> verifyCoverage(claim));
            boolean coverageVerified = false;

            try {
                coverageVerified = future.get(VERIFICATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                logger.error("Coverage verification timed out for claim {}", claim.getId());
                future.cancel(true);
                throw new ClaimProcessingException("Coverage verification timed out");
            }

            if (!coverageVerified) {
                logger.warn("Coverage verification failed for claim {}", claim.getId());
                claim.setStatus(ClaimStatus.REJECTED);
                claim.setNotes("Coverage verification failed - please verify policy details");
                throw new ClaimProcessingException("Coverage verification failed");
            }

            logger.info("Coverage verified successfully for claim {}", claim.getId());
            claim.setStatus(ClaimStatus.COVERAGE_VERIFIED);
            claim.setNotes("Coverage verified successfully");

        } catch (ClaimProcessingException e) {
            throw e; // Re-throw claim-specific exceptions
        } catch (Exception e) {
            logger.error("Unexpected error verifying coverage for claim {}: {}", claim.getId(), e.getMessage());
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setNotes("Internal error during coverage verification");
            throw new ClaimProcessingException("Coverage verification failed due to internal error", e);
        }
    }

    /**
     * Verifies insurance coverage by making external API calls to the insurance provider.
     *
     * @param claim The claim to verify coverage for
     * @return true if coverage is verified, false otherwise
     */
    private boolean verifyCoverage(InsuranceClaim claim) {
        try {
            logger.debug("Making API call to verify coverage for policy {}", claim.getPolicyNumber());

            // Simulate API call to insurance provider
            Thread.sleep(2000); // Simulate network delay

            // In a real implementation, this would:
            // 1. Make HTTP/SOAP call to insurance provider's API
            // 2. Verify policy is active
            // 3. Check coverage limits and restrictions
            // 4. Validate service codes against policy

            // For demo purposes, reject claims with policy numbers starting with "X"
            if (claim.getPolicyNumber().startsWith("X")) {
                logger.warn("Policy {} is flagged for rejection", claim.getPolicyNumber());
                return false;
            }

            return true;
        } catch (InterruptedException e) {
            logger.error("Coverage verification interrupted for claim {}", claim.getId());
            Thread.currentThread().interrupt(); // Restore interrupted status
            return false;
        } catch (Exception e) {
            logger.error("Error during coverage verification API call: {}", e.getMessage());
            return false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } finally {
            super.finalize();
        }
    }
}

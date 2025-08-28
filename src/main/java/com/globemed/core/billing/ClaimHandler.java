package com.globemed.core.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for the Chain of Responsibility pattern in insurance claim processing.
 * Each handler in the chain is responsible for a specific aspect of claim processing.
 */
public abstract class ClaimHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClaimHandler.class);
    protected ClaimHandler nextHandler;

    public void setNext(ClaimHandler handler) {
        this.nextHandler = handler;
    }

    /**
     * Template method that defines the claim processing structure
     * @param claim The insurance claim to process
     * @throws ClaimProcessingException if there's an error during claim processing
     */
    public final void process(InsuranceClaim claim) {
        try {
            logger.info("Processing claim {} in handler {}", claim.getId(), this.getClass().getSimpleName());
            processInternal(claim);
            handleNext(claim);
        } catch (Exception e) {
            logger.error("Error processing claim {} in handler {}: {}",
                claim.getId(), this.getClass().getSimpleName(), e.getMessage());
            claim.setStatus(ClaimStatus.REJECTED);
            throw new ClaimProcessingException("Failed to process claim in " +
                this.getClass().getSimpleName(), e);
        }
    }

    /**
     * Internal processing method to be implemented by concrete handlers
     * @param claim The insurance claim to process
     */
    protected abstract void processInternal(InsuranceClaim claim);

    protected void handleNext(InsuranceClaim claim) {
        if (nextHandler != null) {
            nextHandler.process(claim);
        } else {
            logger.debug("Reached end of claim processing chain for claim {}", claim.getId());
        }
    }
}

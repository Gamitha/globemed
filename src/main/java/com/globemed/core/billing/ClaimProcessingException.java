package com.globemed.core.billing;

/**
 * Custom exception for handling errors during insurance claim processing.
 * Used throughout the Chain of Responsibility pattern to handle specific claim processing failures.
 */
public class ClaimProcessingException extends RuntimeException {
    public ClaimProcessingException(String message) {
        super(message);
    }

    public ClaimProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

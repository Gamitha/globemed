package com.globemed.core.controller;

import com.globemed.core.billing.*;
import com.globemed.core.model.Bill;
import com.globemed.core.model.BillStatus;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;
import com.globemed.core.repository.BillingRepository;
import com.globemed.core.util.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BillingController {
    private static final Logger logger = Logger.getLogger(BillingController.class);
    private final SecurityService securityService;
    private final BillingRepository billingRepository;

    public BillingController(SecurityService securityService) {
        this.securityService = securityService;
        this.billingRepository = new BillingRepository();
    }

    public void createBill(Bill bill) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:create",
            Set.of(Permission.WRITE)
        );
        billingRepository.saveBill(bill);
    }

    public Optional<Bill> getBill(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return billingRepository.findBillById(id.toString());
    }

    public List<Bill> getAllBills() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return billingRepository.findAllBills();
    }

    public void updateBill(Bill bill) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:update",
            Set.of(Permission.WRITE)
        );
        billingRepository.saveBill(bill);
    }

    /**
     * Validates if a bill amount is within acceptable claim limits
     * @param bill The bill to validate
     * @throws IllegalArgumentException if amount is outside valid range
     */
    private void validateBillAmount(Bill bill) {
        BigDecimal amount = bill.getTotalAmount();
        if (amount.compareTo(new BigDecimal("100000.00")) > 0) {
            throw new IllegalArgumentException(
                "Bill amount $" + amount + " exceeds maximum claim limit of $100,000.00. " +
                "Please split into multiple claims or contact insurance provider for special processing."
            );
        }
        if (amount.compareTo(new BigDecimal("10.00")) < 0) {
            throw new IllegalArgumentException(
                "Bill amount $" + amount + " is below minimum claim threshold of $10.00"
            );
        }
    }

    public void submitClaim(Bill bill) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "claim:submit",
            Set.of(Permission.WRITE)
        );

        try {
            // Validate amount before processing
            validateBillAmount(bill);

            // Create and submit the claim
            InsuranceClaim claim = new InsuranceClaim.Builder()
                .withBill(bill)
                .withAmount(bill.getTotalAmount())
                .withInsuranceProvider(bill.getInsuranceProvider())
                .withPolicyNumber(bill.getInsurancePolicyNumber())
                .build();

            // Update bill status
            bill.setStatus(BillStatus.INSURANCE_SUBMITTED);
            billingRepository.saveBill(bill);  // Save the updated bill status

            // Use builder pattern to construct the chain of responsibility
            ClaimHandler claimProcessor = BillingChainBuilder.getDefaultChain().build();

            // Process the claim through the chain
            try {
                claimProcessor.process(claim);
                billingRepository.saveInsuranceClaim(claim);

                // Update bill status based on claim processing result
                switch (claim.getStatus()) {
                    case APPROVED:
                        bill.setStatus(BillStatus.INSURANCE_APPROVED);
                        bill.setCoveredAmount(claim.getApprovedAmount());
                        break;
                    case REJECTED:
                        bill.setStatus(BillStatus.INSURANCE_REJECTED);
                        break;
                    case PENDING_REVIEW:
                        bill.setStatus(BillStatus.INSURANCE_PENDING);
                        break;
                    default:
                        logger.warn("Unexpected claim status {} for bill {}", claim.getStatus(), bill.getId());
                }

                billingRepository.saveBill(bill);

            } catch (ClaimProcessingException e) {
                String errorMessage = String.format(
                    "Unable to process claim: %s. Please verify the information and try again.",
                    e.getMessage()
                );
                logger.error("Error processing insurance claim for bill {}: {}", bill.getId(), e.getMessage());
                bill.setStatus(BillStatus.ERROR);
                bill.setNotes(errorMessage);
                billingRepository.saveBill(bill);
                throw new RuntimeException(errorMessage, e);
            }

        } catch (IllegalArgumentException e) {
            logger.error("Validation error for bill {}: {}", bill.getId(), e.getMessage());
            bill.setStatus(BillStatus.ERROR);
            bill.setNotes(e.getMessage());
            billingRepository.saveBill(bill);
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error processing claim for bill {}: {}", bill.getId(), e.getMessage());
            bill.setStatus(BillStatus.ERROR);
            bill.setNotes("An unexpected error occurred while processing the claim");
            billingRepository.saveBill(bill);
            throw new RuntimeException("Failed to process insurance claim", e);
        }
    }

    public Optional<InsuranceClaim> getClaim(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "claim:read",
            Set.of(Permission.READ)
        );
        return billingRepository.findClaimById(id.toString());
    }

    public List<InsuranceClaim> getAllClaims() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "claim:read",
            Set.of(Permission.READ)
        );
        return billingRepository.findAllClaims();
    }

    public List<InsuranceClaim> getClaimsByStatus(ClaimStatus status) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "claim:read",
            Set.of(Permission.READ)
        );
        return billingRepository.findAllClaims().stream()
                .filter(claim -> claim.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void deleteBill(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:delete",
            Set.of(Permission.WRITE)
        );
        Optional<Bill> bill = getBill(id);
        if (bill.isPresent()) {
            billingRepository.deleteBill(id.toString());
        }
    }

    public void deleteClaim(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "claim:delete",
            Set.of(Permission.WRITE)
        );
        Optional<InsuranceClaim> claim = getClaim(id);
        if (claim.isPresent()) {
            billingRepository.deleteClaim(id.toString());
        }
    }

    public List<Bill> getBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return billingRepository.findAllBills().stream()
                .filter(bill -> !bill.getDateCreated().isBefore(startDate) &&
                              !bill.getDateCreated().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Bill> getBillsByPatient(UUID patientId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return billingRepository.getBillsByPatient(patientId);
    }
}

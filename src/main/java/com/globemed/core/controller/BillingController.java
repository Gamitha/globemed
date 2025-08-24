package com.globemed.core.controller;

import com.globemed.core.billing.*;
import com.globemed.core.model.Bill;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BillingController {
    private final SecurityService securityService;
    private final Map<UUID, Bill> bills;
    private final Map<UUID, InsuranceClaim> claims;
    private final ClaimHandler claimProcessor;

    public BillingController(SecurityService securityService) {
        this.securityService = securityService;
        this.bills = new ConcurrentHashMap<>();
        this.claims = new ConcurrentHashMap<>();

        // Set up claim processing chain
        ClaimHandler validationHandler = new ValidationHandler();
        ClaimHandler coverageHandler = new CoverageVerificationHandler();
        ClaimHandler approvalHandler = new AmountApprovalHandler();
        ClaimHandler settlementHandler = new SettlementHandler();

        validationHandler.setNext(coverageHandler);
        coverageHandler.setNext(approvalHandler);
        approvalHandler.setNext(settlementHandler);

        this.claimProcessor = validationHandler;
    }

    public void createBill(Bill bill) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:create",
            Set.of(Permission.WRITE)
        );
        bills.put(bill.getId(), bill);
    }

    public Optional<Bill> getBill(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return Optional.ofNullable(bills.get(id));
    }

    public List<Bill> getAllBills() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return new ArrayList<>(bills.values());
    }

    public InsuranceClaim submitClaim(Bill bill) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:submit_claim",
            Set.of(Permission.WRITE)
        );

        InsuranceClaim claim = new InsuranceClaim.Builder()
            .withBill(bill)
            .build();

        claims.put(claim.getId(), claim);
        claimProcessor.processRequest(claim);
        return claim;
    }

    public List<InsuranceClaim> getAllClaims() {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return new ArrayList<>(claims.values());
    }

    public Optional<InsuranceClaim> getClaim(UUID id) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return Optional.ofNullable(claims.get(id));
    }

    public List<Bill> getBillsByPatient(UUID patientId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "billing:read",
            Set.of(Permission.READ)
        );
        return bills.values().stream()
            .filter(bill -> bill.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }
}

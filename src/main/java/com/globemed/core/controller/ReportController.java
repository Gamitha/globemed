package com.globemed.core.controller;

import com.globemed.core.model.*;
import com.globemed.core.report.*;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportController {
    private final SecurityService securityService;
    private final PatientController patientController;
    private final BillingController billingController;
    private final List<ReportVisitor> visitors;

    public ReportController(SecurityService securityService,
                          PatientController patientController,
                          BillingController billingController) {
        this.securityService = securityService;
        this.patientController = patientController;
        this.billingController = billingController;
        this.visitors = new ArrayList<>();
        initializeVisitors();
    }

    private void initializeVisitors() {
        visitors.add(new DiagnosticReport());
        visitors.add(new FinancialReport());
        visitors.add(new TreatmentSummaryReport());
    }

    private Optional<Patient> getPatient(UUID patientId) {
        return patientController.getPatient(patientId.toString());
    }

    public List<String> generatePatientReport(UUID patientId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "report:generate",
            Set.of(Permission.READ)
        );

        Optional<Patient> patient = getPatient(patientId);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }

        List<String> reportSections = new ArrayList<>();
        for (ReportVisitor visitor : visitors) {
            reportSections.add(visitor.visit(patient.get()));
        }
        return reportSections;
    }

    public List<String> generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "report:generate",
            Set.of(Permission.READ)
        );

        List<Bill> bills = billingController.getBillsByDateRange(startDate, endDate);
        FinancialReport report = new FinancialReport();
        return bills.stream()
                .map(report::visit)
                .collect(Collectors.toList());
    }

    public List<String> generateBillingReport(UUID patientId) {
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "report:generate",
            Set.of(Permission.READ)
        );

        List<Bill> bills = billingController.getBillsByPatient(patientId);
        FinancialReport report = new FinancialReport();
        return bills.stream()
                .map(report::visit)
                .collect(Collectors.toList());
    }
}

package com.globemed.core.controller;

import com.globemed.core.model.Patient;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Bill;
import com.globemed.core.report.ReportVisitor;
import com.globemed.core.report.TreatmentSummaryReport;
import com.globemed.core.report.FinancialReport;
import com.globemed.core.report.DiagnosticReport;
import com.globemed.core.security.Permission;
import com.globemed.core.security.SecurityService;

import java.util.*;

public class ReportController {
    private final SecurityService securityService;
    private final PatientController patientController;
    private final BillingController billingController;

    public ReportController(SecurityService securityService,
                          PatientController patientController,
                          BillingController billingController) {
        this.securityService = securityService;
        this.patientController = patientController;
        this.billingController = billingController;
    }

    public enum ReportType {
        TREATMENT_SUMMARY,
        FINANCIAL,
        DIAGNOSTIC
    }

    public String generateReport(UUID patientId, ReportType type) {
        // Check permissions
        securityService.checkAccess(
            securityService.getCurrentUser(),
            "report:generate",
            Set.of(Permission.READ)
        );

        Optional<Patient> patientOpt = patientController.getPatient(patientId);
        if (patientOpt.isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }

        Patient patient = patientOpt.get();
        ReportVisitor visitor;

        switch (type) {
            case TREATMENT_SUMMARY:
                visitor = new TreatmentSummaryReport();
                break;
            case FINANCIAL:
                visitor = new FinancialReport();
                break;
            case DIAGNOSTIC:
                visitor = new DiagnosticReport();
                break;
            default:
                throw new IllegalArgumentException("Unsupported report type");
        }

        // Apply visitor pattern to generate report
        visitor.visitPatient(patient);
        if (patient.getMedicalRecords() != null) {
            for (MedicalRecord record : patient.getMedicalRecords()) {
                visitor.visitMedicalRecord(record);
            }
        }

        // Get bills from billing controller if needed
        List<Bill> bills = billingController.getBillsByPatient(patientId);
        if (bills != null && !bills.isEmpty()) {
            for (Bill bill : bills) {
                visitor.visitBill(bill);
            }
        }

        // Generate the final report
        return visitor.generateReport();
    }
}

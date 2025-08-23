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
                throw new IllegalArgumentException("Unknown report type");
        }

        // Visit patient data
        visitor.visitPatient(patient);

        // Visit medical records if patient has any
        for (MedicalRecord record : patient.getMedicalRecords()) {
            visitor.visitMedicalRecord(record);
        }

        // Visit bills for financial reports
        if (type == ReportType.FINANCIAL) {
            for (Bill bill : billingController.getAllBills()) {
                if (bill.getPatientId().equals(patientId)) {
                    visitor.visitBill(bill);
                }
            }
        }

        return visitor.generateReport();
    }

    public enum ReportType {
        TREATMENT_SUMMARY("Treatment Summary"),
        FINANCIAL("Financial Statement"),
        DIAGNOSTIC("Diagnostic Report");

        private final String displayName;

        ReportType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

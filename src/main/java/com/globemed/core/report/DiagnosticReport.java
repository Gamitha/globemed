package com.globemed.core.report;

import com.globemed.core.model.Bill;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Patient;

public class DiagnosticReport implements ReportVisitor {
    @Override
    public String visit(Patient patient) {
        StringBuilder report = new StringBuilder();
        report.append("Diagnostic Report for Patient: ")
              .append(patient.getFirstName())
              .append(" ")
              .append(patient.getLastName())
              .append("\n\n");

        if (patient.getMedicalRecords().isEmpty()) {
            report.append("No diagnostic records found.");
            return report.toString();
        }

        for (MedicalRecord record : patient.getMedicalRecords()) {
            report.append("Date: ").append(record.getDate()).append("\n")
                  .append("Diagnosis: ").append(record.getDiagnosis()).append("\n")
                  .append("Tests Performed: ").append(record.getTestsPerformed()).append("\n")
                  .append("Test Results: ").append(record.getTestResults()).append("\n")
                  .append("Recommendations: ").append(record.getRecommendations()).append("\n")
                  .append("Notes: ").append(record.getNotes()).append("\n\n");
        }

        return report.toString();
    }

    @Override
    public String visit(Bill bill) {
        // Not relevant for diagnostic reports
        return "";
    }

    @Override
    public ReportType getReportType() {
        return ReportType.DIAGNOSTIC;
    }
}

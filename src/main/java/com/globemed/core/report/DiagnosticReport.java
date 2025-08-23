package com.globemed.core.report;

import com.globemed.core.model.Patient;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Bill;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiagnosticReport implements ReportVisitor {
    private final StringBuilder report = new StringBuilder();
    private final List<MedicalRecord> records = new ArrayList<>();
    private Patient currentPatient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void visitPatient(Patient patient) {
        currentPatient = patient;
        report.append("Diagnostic Report\n");
        report.append("================\n\n");
        report.append("Patient Information:\n");
        report.append("-------------------\n");
        report.append("Name: ").append(patient.getFirstName())
              .append(" ").append(patient.getLastName()).append("\n");
        report.append("DOB: ").append(patient.getDateOfBirth().format(DATE_FORMATTER)).append("\n");
        report.append("Contact: ").append(patient.getContactNumber()).append("\n\n");
    }

    @Override
    public void visitMedicalRecord(MedicalRecord record) {
        records.add(record);
    }

    @Override
    public void visitBill(Bill bill) {
        // Not needed for diagnostic report
    }

    @Override
    public String generateReport() {
        if (currentPatient == null) {
            throw new IllegalStateException("No patient data available for report generation");
        }

        report.append("Diagnostic History:\n");
        report.append("-----------------\n\n");

        if (records.isEmpty()) {
            report.append("No diagnostic records found for this patient.\n");
        } else {
            // Get sorted records and group by date
            List<MedicalRecord> sortedRecords = records.stream()
                .sorted((r1, r2) -> r2.getDateTime().compareTo(r1.getDateTime()))
                .collect(Collectors.toList());

            for (MedicalRecord record : sortedRecords) {
                report.append("Date: ").append(record.getDateTime().format(DATE_FORMATTER))
                      .append("\n-----------------------------------------\n")
                      .append("Diagnosis: ").append(record.getDiagnosis())
                      .append("\n\nTests Performed: ").append(record.getTestsPerformed())
                      .append("\nTest Results: ").append(record.getTestResults())
                      .append("\n\nTreatment Plan: ").append(record.getTreatment())
                      .append("\nRecommendations: ").append(record.getRecommendations())
                      .append("\nNotes: ").append(record.getNotes())
                      .append("\n\n");
            }

            // Add statistics
            report.append("\nSummary Statistics:\n")
                  .append("------------------\n")
                  .append("Total Records: ").append(records.size())
                  .append("\nMost Recent Visit: ").append(sortedRecords.get(0).getDateTime().format(DATE_FORMATTER))
                  .append("\nOldest Record: ").append(sortedRecords.get(sortedRecords.size() - 1).getDateTime().format(DATE_FORMATTER))
                  .append("\n");
        }

        report.append("\nReport generated on: ")
              .append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")))
              .append("\n");

        return report.toString();
    }
}

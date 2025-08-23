package com.globemed.core.report;

import com.globemed.core.model.Patient;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Bill;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreatmentSummaryReport implements ReportVisitor {
    private final StringBuilder report = new StringBuilder();
    private final List<MedicalRecord> records = new ArrayList<>();
    private Patient currentPatient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void visitPatient(Patient patient) {
        currentPatient = patient;
        report.append("Treatment Summary Report\n");
        report.append("======================\n\n");
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
        // Not needed for treatment summary
    }

    @Override
    public String generateReport() {
        if (currentPatient == null) {
            throw new IllegalStateException("No patient data available for report generation");
        }

        report.append("Treatment History:\n");
        report.append("----------------\n");

        if (records.isEmpty()) {
            report.append("\nNo treatment records found for this patient.\n");
        } else {
            // Sort records by date (newest first) and generate report
            records.stream()
                  .sorted((r1, r2) -> r2.getDateTime().compareTo(r1.getDateTime()))
                  .forEach(record -> {
                      report.append("\nDate: ").append(record.getDateTime().format(DATE_FORMATTER))
                            .append("\n-----------------------------------------")
                            .append("\nDiagnosis: ").append(record.getDiagnosis())
                            .append("\nTreatment: ").append(record.getTreatment() != null ? record.getTreatment() : "No treatment specified")
                            .append("\nNotes: ").append(record.getNotes() != null ? record.getNotes() : "No additional notes")
                            .append("\n");
                  });

            // Add treatment summary statistics
            report.append("\nTreatment Summary Statistics:\n")
                  .append("-------------------------\n")
                  .append("Total Visits: ").append(records.size());

            // Get date range of treatments
            if (!records.isEmpty()) {
                List<MedicalRecord> sortedRecords = records.stream()
                    .sorted((r1, r2) -> r2.getDateTime().compareTo(r1.getDateTime()))
                    .collect(Collectors.toList());

                report.append("\nFirst Visit: ")
                      .append(sortedRecords.get(sortedRecords.size() - 1).getDateTime().format(DATE_FORMATTER))
                      .append("\nMost Recent Visit: ")
                      .append(sortedRecords.get(0).getDateTime().format(DATE_FORMATTER));
            }
        }

        report.append("\n\nReport generated on: ")
              .append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")))
              .append("\n");

        return report.toString();
    }
}

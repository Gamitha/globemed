package com.globemed.core.report;

import com.globemed.core.model.Bill;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Patient;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TreatmentSummaryReport implements ReportVisitor {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String visit(Patient patient) {
        StringBuilder report = new StringBuilder();
        report.append("Treatment Summary Report\n")
              .append("======================\n\n")
              .append("Patient: ").append(patient.getFirstName())
              .append(" ").append(patient.getLastName())
              .append(" (ID: ").append(patient.getId().toString()).append(")\n\n");

        List<MedicalRecord> records = patient.getMedicalRecords().stream()
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .collect(Collectors.toList());

        if (records.isEmpty()) {
            report.append("No treatment records found.");
            return report.toString();
        }

        // Group treatments by diagnosis
        Map<String, List<MedicalRecord>> byDiagnosis = records.stream()
                .collect(Collectors.groupingBy(MedicalRecord::getDiagnosis));

        byDiagnosis.forEach((diagnosis, diagnosisRecords) -> {
            report.append("\nCondition: ").append(diagnosis).append("\n")
                  .append("----------------------------------------\n");

            diagnosisRecords.forEach(record -> {
                report.append("Date: ").append(record.getDate().format(DATE_FORMAT)).append("\n")
                      .append("Treatment: ").append(record.getTreatment()).append("\n")
                      .append("Results: ").append(record.getTestResults()).append("\n")
                      .append("Recommendations: ").append(record.getRecommendations()).append("\n")
                      .append("----------------------------------------\n");
            });
        });

        // Add treatment progression summary
        report.append("\nTreatment Progression Summary:\n");
        String currentDiagnosis = null;
        for (MedicalRecord record : records) {
            if (!record.getDiagnosis().equals(currentDiagnosis)) {
                currentDiagnosis = record.getDiagnosis();
                report.append("\n").append(currentDiagnosis).append(":\n");
            }
            report.append("- ").append(record.getDate().format(DATE_FORMAT))
                  .append(": ").append(record.getTreatment()).append("\n");
        }

        return report.toString();
    }

    @Override
    public String visit(Bill bill) {
        // Treatment summary doesn't need bill details
        return "";
    }

    @Override
    public ReportType getReportType() {
        return ReportType.TREATMENT_SUMMARY;
    }
}

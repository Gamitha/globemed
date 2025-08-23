package com.globemed.core.report;

import com.globemed.core.model.Patient;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Bill;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FinancialReport implements ReportVisitor {
    private final StringBuilder report = new StringBuilder();
    private final List<Bill> bills = new ArrayList<>();
    private Patient currentPatient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @Override
    public void visitPatient(Patient patient) {
        currentPatient = patient;
        report.append("Financial Statement\n");
        report.append("==================\n\n");
        report.append("Patient: ").append(patient.getFirstName())
              .append(" ").append(patient.getLastName()).append("\n");
        report.append("Insurance Provider: ").append(patient.getInsuranceProvider())
              .append("\nPolicy Number: ").append(patient.getInsurancePolicy()).append("\n\n");
    }

    @Override
    public void visitMedicalRecord(MedicalRecord record) {
        // Not needed for financial report
    }

    @Override
    public void visitBill(Bill bill) {
        bills.add(bill);
    }

    @Override
    public String generateReport() {
        if (currentPatient == null) {
            throw new IllegalStateException("No patient data available for report generation");
        }

        report.append("Billing History:\n");
        report.append("--------------\n\n");

        // Get sorted bills
        List<Bill> sortedBills = bills.stream()
            .sorted((b1, b2) -> b2.getDateTime().compareTo(b1.getDateTime()))
            .collect(Collectors.toList());

        // Calculate totals
        BigDecimal totalCharges = BigDecimal.ZERO;
        BigDecimal totalCovered = BigDecimal.ZERO;
        BigDecimal totalRemaining = BigDecimal.ZERO;

        // Generate bill details and calculate totals
        for (Bill bill : sortedBills) {
            report.append("Date: ").append(bill.getDateTime().format(DATE_FORMATTER))
                  .append("\nBill ID: ").append(bill.getId())
                  .append("\nTotal Amount: $").append(formatCurrency(bill.getTotalAmount()))
                  .append("\nCovered Amount: $").append(formatCurrency(bill.getCoveredAmount()))
                  .append("\nRemaining Balance: $").append(formatCurrency(bill.getRemainingAmount()))
                  .append("\nStatus: ").append(bill.getStatus())
                  .append("\nItems: ").append(bill.getItems().size())
                  .append("\n\n");

            totalCharges = totalCharges.add(bill.getTotalAmount());
            totalCovered = totalCovered.add(bill.getCoveredAmount());
            totalRemaining = totalRemaining.add(bill.getRemainingAmount());
        }

        // Generate summary section
        report.append("\nFinancial Summary\n");
        report.append("----------------\n");
        report.append("Total Charges: $").append(formatCurrency(totalCharges)).append("\n");
        report.append("Total Insurance Coverage: $").append(formatCurrency(totalCovered)).append("\n");
        report.append("Total Outstanding Balance: $").append(formatCurrency(totalRemaining)).append("\n");

        // Calculate coverage percentage if there are charges
        if (totalCharges.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal coveragePercentage = totalCovered
                .multiply(new BigDecimal("100"))
                .divide(totalCharges, 2, RoundingMode.HALF_UP);
            report.append("Insurance Coverage Percentage: ").append(coveragePercentage).append("%\n");
        }

        report.append("\nReport generated on: ")
              .append(java.time.LocalDateTime.now().format(DATE_FORMATTER))
              .append("\n");

        return report.toString();
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f", amount.setScale(2, RoundingMode.HALF_UP));
    }
}

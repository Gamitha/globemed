package com.globemed.core.report;

import com.globemed.core.model.Bill;
import com.globemed.core.model.BillItem;
import com.globemed.core.model.Patient;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FinancialReport implements ReportVisitor {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String visit(Patient patient) {
        StringBuilder report = new StringBuilder();
        report.append("Financial Summary for Patient: ")
              .append(patient.getFirstName())
              .append(" ")
              .append(patient.getLastName())
              .append("\n\n");

        report.append("Insurance Information:\n")
              .append("Provider: ").append(patient.getInsuranceProvider()).append("\n")
              .append("Policy Number: ").append(patient.getInsurancePolicy()).append("\n\n");

        return report.toString();
    }

    @Override
    public String visit(Bill bill) {
        StringBuilder report = new StringBuilder();
        report.append("Bill Details:\n")
              .append("Bill ID: ").append(bill.getId().toString()).append("\n")
              .append("Date: ").append(bill.getDateCreated().format(DATE_FORMAT)).append("\n")
              .append("Status: ").append(bill.getStatus()).append("\n\n");

        report.append("Items:\n");
        for (BillItem item : bill.getItems()) {
            report.append(String.format("- %s (Code: %s) x%d @ $%s = $%s\n",
                item.getDescription(),
                item.getServiceCode(),
                item.getQuantity(),
                item.getUnitPrice().toString(),
                item.getTotal().toString()));
        }

        report.append("\nTotal Amount: $").append(bill.getTotalAmount().toString())
              .append("\nCovered Amount: $").append(bill.getCoveredAmount().toString())
              .append("\nRemaining Amount: $").append(bill.getRemainingAmount().toString())
              .append("\n");

        if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
            report.append("\nNotes: ").append(bill.getNotes()).append("\n");
        }

        return report.toString();
    }

    @Override
    public ReportType getReportType() {
        return ReportType.FINANCIAL;
    }

    public String generateSummary(List<Bill> bills) {
        if (bills.isEmpty()) {
            return "No bills to report.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Financial Summary Report\n")
               .append("======================\n\n");

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCovered = BigDecimal.ZERO;
        BigDecimal totalRemaining = BigDecimal.ZERO;

        // Sort bills by date
        List<Bill> sortedBills = bills.stream()
                .sorted((b1, b2) -> b2.getDateCreated().compareTo(b1.getDateCreated()))
                .collect(Collectors.toList());

        for (Bill bill : sortedBills) {
            summary.append(visit(bill)).append("\n---\n");
            totalAmount = totalAmount.add(bill.getTotalAmount());
            totalCovered = totalCovered.add(bill.getCoveredAmount());
            totalRemaining = totalRemaining.add(bill.getRemainingAmount());
        }

        summary.append("\nSummary Statistics:\n")
               .append("Total Bills: ").append(bills.size()).append("\n")
               .append("Total Amount: $").append(totalAmount).append("\n")
               .append("Total Covered: $").append(totalCovered).append("\n")
               .append("Total Remaining: $").append(totalRemaining).append("\n");

        return summary.toString();
    }
}

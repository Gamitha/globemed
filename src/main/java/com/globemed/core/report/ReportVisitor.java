package com.globemed.core.report;

import com.globemed.core.model.Patient;
import com.globemed.core.model.Bill;

public interface ReportVisitor {
    String visit(Patient patient);
    String visit(Bill bill);
    ReportType getReportType();
}

package com.globemed.core.report;

import com.globemed.core.model.Patient;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Bill;

public interface ReportVisitor {
    void visitPatient(Patient patient);
    void visitMedicalRecord(MedicalRecord record);
    void visitBill(Bill bill);
    String generateReport();
}

package com.globemed.core.view;

import com.globemed.core.billing.ClaimProcessingException;
import com.globemed.core.model.Bill;
import com.globemed.core.util.Logger;
import com.globemed.core.controller.BillingController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.util.DataChangeListener;

import javax.swing.*;
import java.awt.*;

public class BillingView extends JPanel implements DataChangeListener {
    private static final Logger logger = Logger.getLogger(BillingView.class);
    private final BillingPanel billingPanel;

    public BillingView(BillingController billingController, PatientController patientController) {
        setLayout(new BorderLayout());
        this.billingPanel = new BillingPanel(billingController, patientController);
        add(billingPanel, BorderLayout.CENTER);
    }

    protected void handleClaimSubmissionError(Exception e, Bill bill) {
        String title;
        String message;
        int messageType;

        if (e.getCause() instanceof IllegalArgumentException) {
            title = "Cannot Submit Claim";
            message = e.getMessage();
            messageType = JOptionPane.WARNING_MESSAGE;
        } else if (e.getCause() instanceof ClaimProcessingException) {
            title = "Claim Processing Error";
            message = e.getMessage();
            messageType = JOptionPane.ERROR_MESSAGE;
        } else {
            title = "System Error";
            message = "An unexpected error occurred while processing the claim. Please try again later.";
            messageType = JOptionPane.ERROR_MESSAGE;
            logger.error("Unexpected error submitting claim for bill {}: {}", bill.getId(), e.getMessage());
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
            this,
            message,
            title,
            messageType
        ));
    }

    @Override
    public void onDataChanged() {
        SwingUtilities.invokeLater(() -> {
            billingPanel.revalidate();
            billingPanel.repaint();
            revalidate();
            repaint();
        });
    }
}

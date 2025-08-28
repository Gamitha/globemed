package com.globemed.core.view;

import com.globemed.core.controller.ReportController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.model.Patient;
import com.globemed.core.report.ReportType;
import com.globemed.core.util.DataChangeListener;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ReportPanel extends JPanel implements DataChangeListener {
    private final ReportController reportController;
    private final PatientController patientController;
    private JComboBox<String> patientSelector;
    private JComboBox<ReportType> reportTypeSelector;
    private JTextArea reportArea;

    public ReportPanel(ReportController reportController, PatientController patientController) {
        this.reportController = reportController;
        this.patientController = patientController;

        // Register as listener for patient data changes
        this.patientController.addListener(this);

        initializeUI();
    }

    @Override
    public void onDataChanged() {
        refreshSelectors();
    }

    private void initializeUI() {
        FormLayout layout = new FormLayout(
            "4dlu, right:pref, 4dlu, 150dlu:grow, 4dlu",
            "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, fill:100dlu:grow"
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Create and add components
        builder.addLabel("Patient:", cc.xy(2, 2));
        patientSelector = createPatientSelector();
        builder.add(patientSelector, cc.xy(4, 2));

        builder.addLabel("Report Type:", cc.xy(2, 4));
        reportTypeSelector = createReportTypeSelector();
        builder.add(reportTypeSelector, cc.xy(4, 4));

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        builder.add(generateButton, cc.xy(4, 6));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        builder.add(scrollPane, cc.xy(4, 8));

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }

    private JComboBox<String> createPatientSelector() {
        JComboBox<String> selector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        selector.setModel(model);
        return selector;
    }

    private JComboBox<ReportType> createReportTypeSelector() {
        JComboBox<ReportType> selector = new JComboBox<>(ReportType.values());
        selector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ReportType) {
                    setText(((ReportType) value).getDisplayName());
                }
                return this;
            }
        });
        return selector;
    }

    private void refreshSelectors() {
        String selectedPatient = (String) patientSelector.getSelectedItem();
        ReportType selectedType = (ReportType) reportTypeSelector.getSelectedItem();

        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) patientSelector.getModel();
        model.removeAllElements();
        model.addElement(""); // Empty option

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        // Restore selections if they still exist
        if (selectedPatient != null && patientMap.containsKey(selectedPatient)) {
            patientSelector.setSelectedItem(selectedPatient);
        }
        if (selectedType != null) {
            reportTypeSelector.setSelectedItem(selectedType);
        }
    }

    private void generateReport() {
        try {
            String selectedPatient = (String) patientSelector.getSelectedItem();
            ReportType selectedType = (ReportType) reportTypeSelector.getSelectedItem();

            if (selectedPatient == null || selectedPatient.isEmpty() || selectedType == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select both patient and report type",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            UUID patientId = patientController.getPatientDisplayMap().get(selectedPatient);
            java.util.List<String> reportSections;

            switch (selectedType) {
                case PATIENT:
                    reportSections = reportController.generatePatientReport(patientId);
                    break;
                case FINANCIAL:
                    reportSections = reportController.generateFinancialReport(
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now()
                    );
                    break;
                case BILLING:
                    reportSections = reportController.generateBillingReport(patientId);
                    break;
                default:
                    throw new IllegalStateException("Unsupported report type: " + selectedType);
            }

            reportArea.setText(String.join("\n\n", reportSections));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error generating report: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

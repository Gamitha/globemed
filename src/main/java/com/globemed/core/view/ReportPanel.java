package com.globemed.core.view;

import com.globemed.core.controller.PatientController;
import com.globemed.core.controller.ReportController;
import com.globemed.core.controller.ReportController.ReportType;
import com.globemed.core.util.DataChangeListener;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.UUID;

public class ReportPanel extends JPanel implements DataChangeListener {
    private final ReportController reportController;
    private final PatientController patientController;

    private JComboBox<String> patientSelector;
    private JComboBox<ReportType> reportTypeSelector;
    private JTextArea reportDisplay;

    public ReportPanel(ReportController reportController, PatientController patientController) {
        this.reportController = reportController;
        this.patientController = patientController;

        // Register as listener for patient updates
        this.patientController.addListener(this);

        initializeUI();
    }

    @Override
    public void onDataChanged() {
        refreshPatientSelector();
    }

    private void refreshPatientSelector() {
        String selectedPatient = (String) patientSelector.getSelectedItem();

        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) patientSelector.getModel();
        model.removeAllElements();
        model.addElement(""); // Empty option for no selection

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        // Restore previous selection if still exists
        if (selectedPatient != null && patientMap.containsKey(selectedPatient)) {
            patientSelector.setSelectedItem(selectedPatient);
        }
    }

    private void initializeUI() {
        FormLayout layout = new FormLayout(
            "4dlu, right:pref, 4dlu, 150dlu:grow, 4dlu",
            "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, fill:200dlu:grow, 4dlu"
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Patient selection
        builder.addLabel("Patient:", cc.xy(2, 2));
        patientSelector = createPatientSelector();
        builder.add(patientSelector, cc.xy(4, 2));

        // Report type selection
        builder.addLabel("Report Type:", cc.xy(2, 4));
        reportTypeSelector = new JComboBox<>(ReportType.values());
        builder.add(reportTypeSelector, cc.xy(4, 4));

        // Generate button
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        builder.add(generateButton, cc.xy(4, 6));

        // Report display area
        reportDisplay = new JTextArea();
        reportDisplay.setEditable(false);
        reportDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportDisplay);
        builder.add(scrollPane, cc.xyw(2, 8, 3));

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);

        // Add export button at the bottom
        JButton exportButton = new JButton("Export to File");
        exportButton.addActionListener(e -> exportReport());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(exportButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JComboBox<String> createPatientSelector() {
        JComboBox<String> selector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        selector.setModel(model);
        selector.setPreferredSize(new Dimension(300, 25));
        return selector;
    }

    private void generateReport() {
        try {
            String selectedPatient = (String) patientSelector.getSelectedItem();

            if (selectedPatient == null || selectedPatient.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please select a patient",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            UUID patientId = patientController.getPatientDisplayMap().get(selectedPatient);
            ReportType reportType = (ReportType) reportTypeSelector.getSelectedItem();

            String report = reportController.generateReport(patientId, reportType);
            reportDisplay.setText(report);
            reportDisplay.setCaretPosition(0); // Scroll to top

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating report: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReport() {
        if (reportDisplay.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please generate a report first",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("report.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.writeString(file.toPath(), reportDisplay.getText());

                JOptionPane.showMessageDialog(this,
                    "Report exported successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

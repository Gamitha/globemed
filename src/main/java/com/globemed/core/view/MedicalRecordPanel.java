package com.globemed.core.view;

import com.globemed.core.controller.DoctorController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.model.MedicalRecord;
import com.globemed.core.model.Patient;
import com.globemed.core.security.Role;
import com.globemed.core.util.DataChangeListener;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MedicalRecordPanel extends JPanel implements DataChangeListener {
    private final PatientController patientController;
    private final DoctorController doctorController;
    private JComboBox<String> patientSelector;
    private JComboBox<String> doctorSelector;
    private JTextArea diagnosisArea;
    private JTextArea treatmentArea;
    private JTextArea testsPerformedArea;
    private JTextArea testResultsArea;
    private JTextArea recommendationsArea;
    private JTextArea notesArea;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private final boolean isAdminUser;

    public MedicalRecordPanel(PatientController patientController, DoctorController doctorController) {
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.patientController.addListener(this);
        this.doctorController.addListener(this);
        this.isAdminUser = hasAdminRole();
        initializeUI();
    }

    @Override
    public void onDataChanged() {
        refreshPatientSelector();
        refreshDoctorSelector();
    }

    private boolean hasAdminRole() {
        return patientController.hasRole(Role.ADMIN);
    }

    private void refreshPatientSelector() {
        String selectedPatient = (String) patientSelector.getSelectedItem();

        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) patientSelector.getModel();
        model.removeAllElements();
        model.addElement(""); // Empty option

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        // Restore selection if still valid
        if (selectedPatient != null && patientMap.containsKey(selectedPatient)) {
            patientSelector.setSelectedItem(selectedPatient);
        }
    }

    private void refreshDoctorSelector() {
        String selectedDoctor = (String) doctorSelector.getSelectedItem();

        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) doctorSelector.getModel();
        model.removeAllElements();
        model.addElement(""); // Empty option

        Map<String, UUID> doctorMap = doctorController.getDoctorDisplayMap();
        doctorMap.keySet().forEach(model::addElement);

        // Restore selection if still valid
        if (selectedDoctor != null && doctorMap.containsKey(selectedDoctor)) {
            doctorSelector.setSelectedItem(selectedDoctor);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Main split pane with adjusted divider
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(6);

        // Create panels
        JPanel formPanel = createFormPanel();
        JPanel historyPanel = createHistoryPanel();

        // Add proper borders and titles
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(6, 6, 6, 6),
            BorderFactory.createTitledBorder("Medical Record Entry")
        ));

        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(6, 6, 6, 6),
            BorderFactory.createTitledBorder("Medical History")
        ));

        // Add panels to split pane with scroll support
        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        FormLayout layout = new FormLayout(
            "8dlu, right:pref, 6dlu, fill:150dlu:grow, 8dlu",  // columns
            "8dlu, p, 4dlu, " +                // Patient selector
            (isAdminUser ? "p, 4dlu, " : "") + // Doctor selector (only for admin)
            "p, 4dlu, fill:40dlu, 6dlu, " +   // Diagnosis
            "p, 4dlu, fill:40dlu, 6dlu, " +   // Treatment
            "p, 4dlu, fill:40dlu, 6dlu, " +   // Tests
            "p, 4dlu, fill:40dlu, 6dlu, " +   // Results
            "p, 4dlu, fill:40dlu, 6dlu, " +   // Recommendations
            "p, 4dlu, fill:40dlu, 6dlu, " +   // Notes
            "p, 8dlu"                          // Buttons
        );

        JPanel panel = new JPanel(layout);
        CellConstraints cc = new CellConstraints();

        // Patient Selection
        patientSelector = createPatientSelector();
        panel.add(new JLabel("Patient:"), cc.xy(2, 2));
        panel.add(patientSelector, cc.xy(4, 2));

        int row = 4;

        // Doctor Selection (only for admin)
        if (isAdminUser) {
            doctorSelector = createDoctorSelector();
            panel.add(new JLabel("Doctor:"), cc.xy(2, row));
            panel.add(doctorSelector, cc.xy(4, row));
            row += 2;
        }

        // Form Fields
        row = addFormField(panel, "Diagnosis:", diagnosisArea = createTextArea(), row);
        row = addFormField(panel, "Treatment:", treatmentArea = createTextArea(), row);
        row = addFormField(panel, "Tests Performed:", testsPerformedArea = createTextArea(), row);
        row = addFormField(panel, "Test Results:", testResultsArea = createTextArea(), row);
        row = addFormField(panel, "Recommendations:", recommendationsArea = createTextArea(), row);
        row = addFormField(panel, "Notes:", notesArea = createTextArea(), row);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton saveButton = new JButton("Save Record");
        JButton clearButton = new JButton("Clear Form");
        saveButton.addActionListener(e -> saveMedicalRecord());
        clearButton.addActionListener(e -> clearForm());

        Dimension buttonSize = new Dimension(100, 25);
        saveButton.setPreferredSize(buttonSize);
        clearButton.setPreferredSize(buttonSize);

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, cc.xyw(2, row, 3, "right, center"));

        return panel;
    }

    private int addFormField(JPanel panel, String label, JTextArea field, int row) {
        CellConstraints cc = new CellConstraints();
        panel.add(new JLabel(label), cc.xy(2, row));

        // Configure text area
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Add scrolling with consistent styling
        JScrollPane scroll = new JScrollPane(field);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        panel.add(scroll, cc.xy(4, row + 2));
        return row + 4;
    }

    private JComboBox<String> createPatientSelector() {
        JComboBox<String> selector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        selector.setModel(model);
        selector.setPreferredSize(new Dimension(300, 25));
        selector.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        selector.addActionListener(e -> {
            String selected = (String) selector.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                refreshRecordsTable(patientController.getPatientDisplayMap().get(selected));
            }
        });

        return selector;
    }

    private JComboBox<String> createDoctorSelector() {
        JComboBox<String> selector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option for no selection

        // Get list of doctors from controller
        Map<String, UUID> doctorMap = doctorController.getDoctorDisplayMap();
        doctorMap.keySet().forEach(model::addElement);

        selector.setModel(model);
        selector.setPreferredSize(new Dimension(300, 25));
        selector.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        return selector;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create table
        createRecordsTable();
        recordsTable.setFillsViewportHeight(true);

        // Set table properties
        recordsTable.setRowHeight(20);
        recordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return area;
    }

    private void createRecordsTable() {
        String[] columnNames = {"Date", "Diagnosis", "Treatment"};
        tableModel = new DefaultTableModel(columnNames, 0);
        recordsTable = new JTable(tableModel);

        // Set column widths
        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(200);

        // Add selection listener with a placeholder for future enhancements
        recordsTable.getSelectionModel().addListSelectionListener(e -> {
            // Placeholder for future record selection handling
            // Will be implemented when record details viewing is added
        });
    }

    private void refreshRecordsTable(UUID patientId) {
        tableModel.setRowCount(0);
        if (patientId != null) {
            Optional<Patient> patient = patientController.getPatient(patientId.toString());
            patient.ifPresent(p -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (MedicalRecord record : p.getMedicalHistory()) {
                    tableModel.addRow(new Object[]{
                        record.getDateTime().format(formatter),
                        record.getDiagnosis(),
                        record.getTreatment()
                    });
                }
            });
        }
    }

    /**
     * Gets the UUID of the currently selected doctor from the doctor selector.
     * Reserved for future use when implementing direct doctor assignment functionality.
     * @return UUID of selected doctor, or null if no doctor selected or user is not admin
     */
    private UUID getSelectedDoctorId() {
        if (!isAdminUser) {
            return null; // Will be handled by the controller for non-admin users
        }
        String selectedDoctor = (String) doctorSelector.getSelectedItem();
        if (selectedDoctor == null || selectedDoctor.isEmpty()) {
            return null;
        }
        return doctorController.getDoctorDisplayMap().get(selectedDoctor);
    }

    /**
     * Refreshes the medical records table based on the currently selected patient.
     * Reserved for future use when implementing bulk record updates or patient switching.
     */
    private void refreshRecordsTable() {
        String selectedPatient = (String) patientSelector.getSelectedItem();
        if (selectedPatient != null && !selectedPatient.isEmpty()) {
            UUID patientId = patientController.getPatientDisplayMap().get(selectedPatient);
            refreshRecordsTable(patientId);
        } else {
            tableModel.setRowCount(0);
        }
    }

    private void saveMedicalRecord() {
        String selectedPatient = (String) patientSelector.getSelectedItem();
        if (selectedPatient == null || selectedPatient.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a patient first",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get doctor ID based on role
        UUID doctorId = null;
        if (isAdminUser) {
            String selectedDoctor = (String) doctorSelector.getSelectedItem();
            if (selectedDoctor == null || selectedDoctor.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please select a doctor",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            doctorId = doctorController.getDoctorDisplayMap().get(selectedDoctor);
        }

        UUID patientId = patientController.getPatientDisplayMap().get(selectedPatient);
        try {
            MedicalRecord record = new MedicalRecord.Builder()
                .withPatientId(patientId)
                .withDoctorId(doctorId)  // Pass the selected doctor's UUID
                .withDiagnosis(diagnosisArea.getText())
                .withTreatment(treatmentArea.getText())
                .withTestsPerformed(testsPerformedArea.getText())
                .withTestResults(testResultsArea.getText())
                .withRecommendations(recommendationsArea.getText())
                .withNotes(notesArea.getText())
                .build();

            if (patientController.addMedicalRecord(patientId, record)) {
                clearForm();
                // Ensure table refresh happens on EDT after successful save
                SwingUtilities.invokeLater(() -> {
                    refreshRecordsTable(patientId);
                    tableModel.fireTableDataChanged();
                });
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving medical record: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        diagnosisArea.setText("");
        treatmentArea.setText("");
        testsPerformedArea.setText("");
        testResultsArea.setText("");
        recommendationsArea.setText("");
        notesArea.setText("");
    }
}

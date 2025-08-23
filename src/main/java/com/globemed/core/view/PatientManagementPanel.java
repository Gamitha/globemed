package com.globemed.core.view;

import com.globemed.core.controller.PatientController;
import com.globemed.core.model.Patient;
import com.globemed.core.util.DatePickerFormatter;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import org.jdatepicker.impl.JDatePickerImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class PatientManagementPanel extends JPanel {
    private final PatientController controller;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JDatePickerImpl dobPicker;
    private JTextField contactField;
    private JTextField emailField;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientManagementPanel(PatientController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        FormLayout layout = new FormLayout(
            "4dlu, right:pref, 4dlu, 150dlu:grow, 4dlu",
            "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, fill:100dlu:grow"
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Initialize components
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        dobPicker = DatePickerFormatter.createDatePicker();
        contactField = new JTextField();
        emailField = new JTextField();

        // Add form fields
        builder.addLabel("First Name:", cc.xy(2, 2));
        builder.add(firstNameField, cc.xy(4, 2));

        builder.addLabel("Last Name:", cc.xy(2, 4));
        builder.add(lastNameField, cc.xy(4, 4));

        builder.addLabel("Date of Birth:", cc.xy(2, 6));
        builder.add(dobPicker, cc.xy(4, 6));

        builder.addLabel("Contact:", cc.xy(2, 8));
        builder.add(contactField, cc.xy(4, 8));

        builder.addLabel("Email:", cc.xy(2, 10));
        builder.add(emailField, cc.xy(4, 10));

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Patient");
        JButton clearButton = new JButton("Clear Form");

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        builder.add(buttonPanel, cc.xy(4, 12));

        // Create patient table
        String[] columnNames = {"ID", "Name", "Date of Birth", "Contact", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);
        builder.add(scrollPane, cc.xy(4, 14));

        // Add action listeners
        addButton.addActionListener(e -> addPatient());
        clearButton.addActionListener(e -> clearForm());

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);

        // Initial table population
        refreshPatientTable();
    }

    private void addPatient() {
        try {
            LocalDate dob = DatePickerFormatter.getLocalDateFromPicker(dobPicker);
            if (dob == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a valid date of birth",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Patient patient = new Patient.Builder()
                .withFirstName(firstNameField.getText())
                .withLastName(lastNameField.getText())
                .withDateOfBirth(dob)
                .withContactNumber(contactField.getText())
                .withEmail(emailField.getText())
                .build();

            controller.addPatient(patient);
            clearForm();
            refreshPatientTable();
            JOptionPane.showMessageDialog(this,
                "Patient added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error adding patient: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobPicker.getModel().setValue(null);
        contactField.setText("");
        emailField.setText("");
    }

    private void refreshPatientTable() {
        tableModel.setRowCount(0);
        for (Patient patient : controller.getAllPatients()) {
            Object[] row = {
                patient.getId(),
                patient.getFirstName() + " " + patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getContactNumber(),
                patient.getEmail()
            };
            tableModel.addRow(row);
        }
    }
}

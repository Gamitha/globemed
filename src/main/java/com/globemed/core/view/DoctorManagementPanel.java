package com.globemed.core.view;

import com.globemed.core.controller.DoctorController;
import com.globemed.core.model.Doctor;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class DoctorManagementPanel extends JPanel {
    private final DoctorController controller;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField specializationField;
    private JTextField contactField;
    private JTextField emailField;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JList<String> workingDaysList;
    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public DoctorManagementPanel(DoctorController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        FormLayout layout = new FormLayout(
            "4dlu, right:pref, 4dlu, 150dlu:grow, 4dlu, pref, 4dlu",
            "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, " +
            "4dlu, pref, 4dlu, 80dlu, 4dlu, pref, 4dlu, fill:100dlu:grow"
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Initialize form components
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        specializationField = new JTextField();
        contactField = new JTextField();
        emailField = new JTextField();

        // Time spinners
        startTimeSpinner = createTimeSpinner(LocalTime.of(9, 0));
        endTimeSpinner = createTimeSpinner(LocalTime.of(17, 0));

        // Working days list
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        workingDaysList = new JList<>(days);
        workingDaysList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane daysScrollPane = new JScrollPane(workingDaysList);

        // Add form fields
        int row = 2;
        builder.addLabel("First Name:", cc.xy(2, row));
        builder.add(firstNameField, cc.xy(4, row));

        row += 2;
        builder.addLabel("Last Name:", cc.xy(2, row));
        builder.add(lastNameField, cc.xy(4, row));

        row += 2;
        builder.addLabel("Specialization:", cc.xy(2, row));
        builder.add(specializationField, cc.xy(4, row));

        row += 2;
        builder.addLabel("Contact:", cc.xy(2, row));
        builder.add(contactField, cc.xy(4, row));

        row += 2;
        builder.addLabel("Email:", cc.xy(2, row));
        builder.add(emailField, cc.xy(4, row));

        row += 2;
        builder.addLabel("Working Hours:", cc.xy(2, row));
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.add(new JLabel("Start:"));
        timePanel.add(startTimeSpinner);
        timePanel.add(new JLabel("End:"));
        timePanel.add(endTimeSpinner);
        builder.add(timePanel, cc.xy(4, row));

        row += 2;
        builder.addLabel("Working Days:", cc.xy(2, row));
        builder.add(daysScrollPane, cc.xy(4, row));

        // Add buttons
        row += 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Doctor");
        JButton clearButton = new JButton("Clear Form");

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        builder.add(buttonPanel, cc.xy(4, row));

        // Create and add doctor table
        row += 2;
        createDoctorTable();
        builder.add(new JScrollPane(doctorTable), cc.xyw(2, row, 5));

        // Add action listeners
        addButton.addActionListener(e -> addDoctor());
        clearButton.addActionListener(e -> clearForm());

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);

        // Initial table population
        refreshDoctorTable();
    }

    private JSpinner createTimeSpinner(LocalTime defaultTime) {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, defaultTime.getHour());
        calendar.set(Calendar.MINUTE, defaultTime.getMinute());
        spinner.setValue(calendar.getTime());
        return spinner;
    }

    private void createDoctorTable() {
        String[] columnNames = {"ID", "Name", "Specialization", "Contact", "Email", "Working Hours", "Working Days"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(tableModel);
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(120);
    }

    private void addDoctor() {
        try {
            // Get selected working days
            List<String> selectedDays = workingDaysList.getSelectedValuesList();
            if (selectedDays.isEmpty()) {
                showError("Please select working days");
                return;
            }

            // Get working hours
            Calendar startCal = Calendar.getInstance();
            startCal.setTime((Date) startTimeSpinner.getValue());
            LocalTime startTime = LocalTime.of(
                startCal.get(Calendar.HOUR_OF_DAY),
                startCal.get(Calendar.MINUTE)
            );

            Calendar endCal = Calendar.getInstance();
            endCal.setTime((Date) endTimeSpinner.getValue());
            LocalTime endTime = LocalTime.of(
                endCal.get(Calendar.HOUR_OF_DAY),
                endCal.get(Calendar.MINUTE)
            );

            // Create and add doctor
            Doctor doctor = new Doctor.Builder()
                .withFirstName(firstNameField.getText())
                .withLastName(lastNameField.getText())
                .withSpecialization(specializationField.getText())
                .withContactNumber(contactField.getText())
                .withEmail(emailField.getText())
                .withWorkingHours(startTime, endTime)
                .withWorkingDays(new HashSet<>(selectedDays))
                .build();

            controller.addDoctor(doctor);
            clearForm();
            refreshDoctorTable();
            showSuccess("Doctor added successfully!");
        } catch (Exception ex) {
            showError("Error adding doctor: " + ex.getMessage());
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        specializationField.setText("");
        contactField.setText("");
        emailField.setText("");
        workingDaysList.clearSelection();

        // Reset time spinners to default values
        Calendar defaultTime = Calendar.getInstance();
        defaultTime.set(Calendar.HOUR_OF_DAY, 9);
        defaultTime.set(Calendar.MINUTE, 0);
        startTimeSpinner.setValue(defaultTime.getTime());
        defaultTime.set(Calendar.HOUR_OF_DAY, 17);
        endTimeSpinner.setValue(defaultTime.getTime());
    }

    private void refreshDoctorTable() {
        tableModel.setRowCount(0);
        for (Doctor doctor : controller.getAllDoctors()) {
            Object[] row = {
                doctor.getId().toString().substring(0, 8),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getContactNumber(),
                doctor.getEmail(),
                String.format("%s - %s",
                    doctor.getStartTime().toString(),
                    doctor.getEndTime().toString()),
                String.join(", ", doctor.getWorkingDays())
            };
            tableModel.addRow(row);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
}

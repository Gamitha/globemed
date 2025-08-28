package com.globemed.core.view;

import com.globemed.core.appointment.Appointment;
import com.globemed.core.appointment.AppointmentMediator;
import com.globemed.core.appointment.DefaultAppointmentMediator;
import com.globemed.core.controller.AppointmentController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.controller.DoctorController;
import com.globemed.core.model.Patient;
import com.globemed.core.util.DatePickerFormatter;
import com.globemed.core.util.DataChangeListener;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import org.jdatepicker.impl.JDatePickerImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;

public class AppointmentPanel extends JPanel implements DataChangeListener {
    private final AppointmentMediator mediator;
    private final AppointmentController appointmentController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private JComboBox<String> patientSelector;
    private JComboBox<String> doctorSelector;
    private JDatePickerImpl datePicker;
    private JSpinner timeSpinner;
    private JTextArea notesArea;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField patientNameField;
    private JTextField patientIdField;

    public AppointmentPanel(AppointmentController appointmentController,
                          PatientController patientController,
                          DoctorController doctorController) {
        this.appointmentController = appointmentController;
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.mediator = new DefaultAppointmentMediator(appointmentController, patientController);

        // Register as listener for both controllers
        patientController.addListener(this);
        doctorController.addListener(this);

        initializeUI();
    }

    @Override
    public void onDataChanged() {
        refreshSelectors();
    }

    private void refreshSelectors() {
        // Store currently selected values
        String selectedPatient = (String) patientSelector.getSelectedItem();
        String selectedDoctor = (String) doctorSelector.getSelectedItem();

        // Refresh patient selector
        DefaultComboBoxModel<String> patientModel = (DefaultComboBoxModel<String>) patientSelector.getModel();
        patientModel.removeAllElements();
        patientModel.addElement(""); // Empty option
        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(patientModel::addElement);

        // Refresh doctor selector
        DefaultComboBoxModel<String> doctorModel = (DefaultComboBoxModel<String>) doctorSelector.getModel();
        doctorModel.removeAllElements();
        doctorModel.addElement(""); // Empty option
        Map<String, UUID> doctorMap = doctorController.getDoctorDisplayMap();
        doctorMap.keySet().forEach(doctorModel::addElement);

        // Restore selections if they still exist
        if (selectedPatient != null && patientMap.containsKey(selectedPatient)) {
            patientSelector.setSelectedItem(selectedPatient);
        }
        if (selectedDoctor != null && doctorMap.containsKey(selectedDoctor)) {
            doctorSelector.setSelectedItem(selectedDoctor);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        FormLayout layout = new FormLayout(
            "4dlu, right:pref, 4dlu, 150dlu:grow, 4dlu, pref, 4dlu",
            "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, 50dlu, 4dlu, pref, 4dlu, fill:100dlu:grow"
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Initialize components with enhanced UI
        patientSelector = createPatientSelector();
        doctorSelector = createDoctorSelector();
        datePicker = DatePickerFormatter.createDatePicker();
        timeSpinner = DatePickerFormatter.createTimeSpinner();
        notesArea = new JTextArea();
        notesArea.setLineWrap(true);

        // Add form fields with improved layout
        builder.addLabel("Patient:", cc.xy(2, 2));
        builder.add(patientSelector, cc.xy(4, 2));

        builder.addLabel("Doctor:", cc.xy(2, 4));
        builder.add(doctorSelector, cc.xy(4, 4));

        // Create date-time panel with better visual grouping
        builder.addLabel("Appointment:", cc.xy(2, 6));
        JPanel dateTimePanel = createDateTimePanel();
        builder.add(dateTimePanel, cc.xy(4, 6));

        builder.addLabel("Notes:", cc.xy(2, 8));
        builder.add(new JScrollPane(notesArea), cc.xy(4, 8));

        // Add action panel with status-aware buttons
        JPanel actionPanel = createActionPanel();
        builder.add(actionPanel, cc.xyw(4, 12, 3));

        // Create appointment table with enhanced rendering
        createAppointmentTable();
        builder.add(new JScrollPane(appointmentTable), cc.xyw(4, 14, 3));

        add(builder.getPanel(), BorderLayout.CENTER);
        refreshAppointmentTable();
    }

    private JComboBox<String> createPatientSelector() {
        patientSelector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option

        // Add all patients from controller
        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        patientSelector.setModel(model);
        patientSelector.setPreferredSize(new Dimension(300, 25));

        return patientSelector;
    }

    private JComboBox<String> createDoctorSelector() {
        doctorSelector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option

        // Add all doctors from controller
        Map<String, UUID> doctorMap = doctorController.getDoctorDisplayMap();
        doctorMap.keySet().forEach(model::addElement);

        doctorSelector.setModel(model);
        doctorSelector.setPreferredSize(new Dimension(300, 25));

        return doctorSelector;
    }

    private JPanel createDateTimePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.add(new JLabel("Date:"));
        panel.add(datePicker);
        panel.add(new JLabel("Time:"));
        panel.add(timeSpinner);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> scheduleAppointment());

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> confirmSelectedAppointment());

        JButton completeButton = new JButton("Complete");
        completeButton.addActionListener(e -> completeSelectedAppointment());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> cancelSelectedAppointment());

        panel.add(scheduleButton);
        panel.add(confirmButton);
        panel.add(completeButton);
        panel.add(cancelButton);

        return panel;
    }

    private void createAppointmentTable() {
        String[] columnNames = {"ID", "Patient", "Doctor", "Date/Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private String getSelectedId(String selectedItem) {
        if (selectedItem == null || selectedItem.isEmpty()) {
            return null;
        }
        return selectedItem.split(" - ")[0];
    }

    private UUID getSelectedPatientId() {
        String selected = (String) patientSelector.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            return null;
        }

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        return patientMap.get(selected);
    }

    private UUID getSelectedDoctorId() {
        String selected = (String) doctorSelector.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            return null;
        }

        Map<String, UUID> doctorMap = doctorController.getDoctorDisplayMap();
        return doctorMap.get(selected);
    }

    private void scheduleAppointment() {
        try {
            LocalDateTime dateTime = DatePickerFormatter.getLocalDateTimeFromPicker(datePicker, timeSpinner);
            if (dateTime == null) {
                showError("Please select a valid date and time");
                return;
            }

            UUID patientId = getSelectedPatientId();
            UUID doctorId = getSelectedDoctorId();

            if (patientId == null || doctorId == null) {
                showError("Please select both patient and doctor");
                return;
            }

            // Use mediator to coordinate scheduling
            mediator.scheduleAppointment(
                patientId,
                doctorId.toString(),
                dateTime,
                notesArea.getText()
            );

            clearForm();
            refreshAppointmentTable();
            showSuccess("Appointment scheduled successfully!");
        } catch (Exception ex) {
            showError("Error scheduling appointment: " + ex.getMessage());
        }
    }

    private void confirmSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                UUID appointmentId = UUID.fromString((String) tableModel.getValueAt(selectedRow, 0));
                appointmentController.confirmAppointment(appointmentId);
                refreshAppointmentTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error confirming appointment: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void completeSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                UUID appointmentId = UUID.fromString((String) tableModel.getValueAt(selectedRow, 0));
                appointmentController.completeAppointment(appointmentId);
                refreshAppointmentTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error completing appointment: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                UUID appointmentId = UUID.fromString((String) tableModel.getValueAt(selectedRow, 0));
                appointmentController.cancelAppointment(appointmentId);
                refreshAppointmentTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error cancelling appointment: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        patientSelector.setSelectedIndex(-1);
        doctorSelector.setSelectedIndex(-1);
        datePicker.getModel().setValue(null);
        timeSpinner.setValue(new java.util.Date());
        notesArea.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAppointmentTable() {
        tableModel.setRowCount(0);
        for (Appointment appointment : appointmentController.getAllAppointments()) {
            Vector<Object> row = new Vector<>();
            row.add(appointment.getId().toString());
            mediator.getPatient(appointment.getPatientId())
                .ifPresent(patient -> row.add(patient.getFirstName() + " " + patient.getLastName()));
            row.add(appointment.getDoctorId());
            row.add(appointment.getScheduledDateTime().toString());
            row.add(appointment.getStatus());
            row.add(appointment.getNotes());
            tableModel.addRow(row);
        }
    }

    private void updatePatientInfo(Optional<Patient> patientOpt) {
        patientOpt.ifPresent(patient -> {
            patientNameField.setText(patient.getFirstName() + " " + patient.getLastName());
            patientIdField.setText(patient.getId().toString());
        });
    }
}

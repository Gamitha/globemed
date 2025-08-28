package com.globemed.core.view;

import com.globemed.core.billing.InsuranceClaim;
import com.globemed.core.controller.BillingController;
import com.globemed.core.controller.PatientController;
import com.globemed.core.model.Bill;
import com.globemed.core.model.BillItem;
import com.globemed.core.model.BillStatus;
import com.globemed.core.model.Patient;
import com.globemed.core.util.DataChangeListener;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BillingPanel extends JPanel implements DataChangeListener {
    private final BillingController billingController;
    private final PatientController patientController;

    private JComboBox<String> patientSelector;
    private JTextField insurancePolicyField;
    private JTextField insuranceProviderField;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private JTextField descriptionField;
    private JTextField serviceCodeField;
    private JSpinner quantitySpinner;
    private JTextField unitPriceField;
    private JTable billsTable;
    private DefaultTableModel billsTableModel;
    private JTextArea notesArea;

    // Additional fields for bill details
    private JTextField patientNameField;
    private JTextField patientIdField;
    private JTextField totalAmountField;
    private JTextField policyNumberField;
    private JButton submitClaimButton;
    private JComboBox<String> billComboBox;

    public BillingPanel(BillingController billingController, PatientController patientController) {
        this.billingController = billingController;
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
            "4dlu, right:pref, 4dlu, 150dlu:grow, 4dlu, pref, 4dlu",
            "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, " +
            "pref, 4dlu, 100dlu, 4dlu, pref, 4dlu, " +
            "pref, 4dlu, pref, 4dlu, 150dlu:grow"  // Added a row for bill selection
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Patient selection and insurance info
        builder.addLabel("Patient:", cc.xy(2, 2));
        patientSelector = createPatientSelector();
        builder.add(patientSelector, cc.xy(4, 2));

        builder.addLabel("Insurance Policy:", cc.xy(2, 4));
        insurancePolicyField = new JTextField();
        builder.add(insurancePolicyField, cc.xy(4, 4));

        builder.addLabel("Insurance Provider:", cc.xy(2, 6));
        insuranceProviderField = new JTextField();
        builder.add(insuranceProviderField, cc.xy(4, 6));

        // Bill items section
        builder.addLabel("Bill Items:", cc.xy(2, 8));
        builder.add(createBillItemsPanel(), cc.xyw(2, 10, 5));

        // Add item button
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> addBillItem());
        builder.add(addItemButton, cc.xy(4, 12));

        // Notes
        builder.addLabel("Notes:", cc.xy(2, 14));
        notesArea = new JTextArea();
        notesArea.setRows(3);
        builder.add(new JScrollPane(notesArea), cc.xy(4, 14));

        // Bill Selection for Claims
        builder.addLabel("Select Bill:", cc.xy(2, 16));
        billComboBox = new JComboBox<>();
        builder.add(billComboBox, cc.xy(4, 16));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton createBillButton = new JButton("Create Bill");
        submitClaimButton = new JButton("Submit Insurance Claim");

        createBillButton.addActionListener(e -> createBill());
        submitClaimButton.addActionListener(e -> submitClaim());

        buttonsPanel.add(createBillButton);
        buttonsPanel.add(submitClaimButton);

        builder.add(buttonsPanel, cc.xyw(2, 18, 5));

        // Bills table
        createBillsTable();
        JPanel tablesPanel = new JPanel(new BorderLayout());
        tablesPanel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);

        refreshBillsTable();
    }

    private void updateInsuranceFields(Patient patient) {
        if (insurancePolicyField != null && patient.getInsurancePolicy() != null) {
            insurancePolicyField.setText(patient.getInsurancePolicy());
            insurancePolicyField.setEditable(false);
        }
        if (insuranceProviderField != null && patient.getInsuranceProvider() != null) {
            insuranceProviderField.setText(patient.getInsuranceProvider());
            insuranceProviderField.setEditable(false);
        }
    }

    private UUID getSelectedPatientId() {
        String selected = (String) patientSelector.getSelectedItem();
        if (selected != null && !selected.isEmpty()) {
            return patientController.getPatientDisplayMap().get(selected);
        }
        return null;
    }

    private JComboBox<String> createPatientSelector() {
        patientSelector = new JComboBox<>();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Empty option

        Map<String, UUID> patientMap = patientController.getPatientDisplayMap();
        patientMap.keySet().forEach(model::addElement);

        patientSelector.setModel(model);
        patientSelector.setPreferredSize(new Dimension(300, 25));

        // Add selection change listener
        patientSelector.addActionListener(e -> {
            UUID patientId = getSelectedPatientId();
            if (patientId != null) {
                Optional<Patient> patient = patientController.getPatient(patientId.toString());
                patient.ifPresent(this::updateInsuranceFields);
            } else {
                clearInsuranceFields();
            }
        });

        return patientSelector;
    }

    private void clearInsuranceFields() {
        if (insurancePolicyField != null) {
            insurancePolicyField.setText("");
        }
        if (insuranceProviderField != null) {
            insuranceProviderField.setText("");
        }
    }

    private JPanel createBillItemsPanel() {
        // Create items table with proper column classes
        String[] columns = {"Description", "Service Code", "Quantity", "Unit Price", "Total"};
        itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // Description
                    case 1: // Service Code
                        return String.class;
                    case 2: // Quantity
                        return Integer.class;
                    case 3: // Unit Price
                    case 4: // Total
                        return BigDecimal.class;
                    default:
                        return Object.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells read-only
            }
        };
        itemsTable = new JTable(itemsTableModel);

        // Set column renderers for proper display
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value instanceof BigDecimal ?
                    String.format("$%.2f", ((BigDecimal) value).doubleValue()) :
                    "");
            }
        });

        itemsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value instanceof BigDecimal ?
                    String.format("$%.2f", ((BigDecimal) value).doubleValue()) :
                    "");
            }
        });

        // Create input fields
        descriptionField = new JTextField(20);
        serviceCodeField = new JTextField(10);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        unitPriceField = new JTextField(10);

        // Layout for input fields
        FormLayout layout = new FormLayout(
            "pref, 4dlu, 50dlu:grow, 4dlu, pref, 4dlu, 30dlu, 4dlu, pref, 4dlu, 50dlu",
            "pref, 4dlu, fill:50dlu:grow"
        );

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Add input fields
        int col = 1;
        builder.addLabel("Description:", cc.xy(col, 1));
        builder.add(descriptionField, cc.xy(col + 2, 1));

        builder.addLabel("Code:", cc.xy(col + 4, 1));
        builder.add(serviceCodeField, cc.xy(col + 6, 1));

        builder.addLabel("Price:", cc.xy(col + 8, 1));
        builder.add(unitPriceField, cc.xy(col + 10, 1));

        // Add table with proper size
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        builder.add(scrollPane, cc.xyw(1, 3, 11));

        return builder.getPanel();
    }

    private void createBillsTable() {
        String[] columns = {"ID", "Patient", "Total Amount", "Insurance Status", "Covered Amount", "Remaining"};
        billsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(billsTableModel);
    }

    private void addBillItem() {
        try {
            if (descriptionField.getText().trim().isEmpty() ||
                serviceCodeField.getText().trim().isEmpty() ||
                unitPriceField.getText().trim().isEmpty()) {
                throw new IllegalStateException("All fields are required");
            }

            BillItem item = new BillItem.Builder()
                .withDescription(descriptionField.getText().trim())
                .withServiceCode(serviceCodeField.getText().trim())
                .withQuantity((Integer) quantitySpinner.getValue())
                .withUnitPrice(new BigDecimal(unitPriceField.getText().trim()))
                .build();

            // Add row with proper data types
            itemsTableModel.addRow(new Object[] {
                item.getDescription(),
                item.getServiceCode(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal()
            });

            // Clear input fields
            descriptionField.setText("");
            serviceCodeField.setText("");
            quantitySpinner.setValue(1);
            unitPriceField.setText("");

            // Debug log
            System.out.println("Added item to table. Current row count: " + itemsTableModel.getRowCount());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error adding item: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createBill() {
        try {
            System.out.println("Current item count: " + itemsTableModel.getRowCount());

            if (itemsTableModel.getRowCount() == 0) {
                throw new IllegalStateException("Bill must have at least one item");
            }

            UUID patientId = getSelectedPatientId();
            if (patientId == null) {
                throw new IllegalStateException("Please select a patient");
            }

            // Start building the bill with patient ID
            Bill.Builder billBuilder = new Bill.Builder()
                .withPatientId(patientId)
                .withInsurancePolicy(insurancePolicyField.getText(), insuranceProviderField.getText())
                .withNotes(notesArea.getText());

            // Convert and add items during bill construction
            System.out.println("Converting table rows to BillItems...");
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                System.out.println("Row " + i + ": " +
                    "Description=" + itemsTableModel.getValueAt(i, 0) + ", " +
                    "Code=" + itemsTableModel.getValueAt(i, 1) + ", " +
                    "Quantity=" + itemsTableModel.getValueAt(i, 2) + ", " +
                    "Price=" + itemsTableModel.getValueAt(i, 3));

                Object quantityObj = itemsTableModel.getValueAt(i, 2);
                Object priceObj = itemsTableModel.getValueAt(i, 3);

                int quantity;
                BigDecimal price;

                // Handle potential type conversion issues
                if (quantityObj instanceof String) {
                    quantity = Integer.parseInt((String) quantityObj);
                } else if (quantityObj instanceof Integer) {
                    quantity = (Integer) quantityObj;
                } else {
                    quantity = 1;
                    System.out.println("Warning: Invalid quantity type: " + quantityObj.getClass());
                }

                if (priceObj instanceof String) {
                    price = new BigDecimal((String) priceObj);
                } else if (priceObj instanceof BigDecimal) {
                    price = (BigDecimal) priceObj;
                } else {
                    price = BigDecimal.ZERO;
                    System.out.println("Warning: Invalid price type: " + priceObj.getClass());
                }

                BillItem item = new BillItem.Builder()
                    .withDescription((String) itemsTableModel.getValueAt(i, 0))
                    .withServiceCode((String) itemsTableModel.getValueAt(i, 1))
                    .withQuantity(quantity)
                    .withUnitPrice(price)
                    .build();

                // Add item to bill during construction
                billBuilder.addItem(item);
            }

            System.out.println("Building bill...");
            Bill bill = billBuilder.build();
            billingController.createBill(bill);

            clearForm();
            refreshBillsTable();

            JOptionPane.showMessageDialog(this,
                "Bill created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error creating bill: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBillSelection(String selectedBillId) {
        if (selectedBillId != null) {
            billingController.getBill(UUID.fromString(selectedBillId)).ifPresent(bill -> {
                updateBillDetails(bill);
                submitClaimButton.setEnabled(true);
            });
        }
    }

    private void updateBillDetails(Bill bill) {
        patientController.getPatient(bill.getPatientId().toString()).ifPresent(patient -> {
            patientNameField.setText(patient.getFirstName() + " " + patient.getLastName());
            patientIdField.setText(patient.getId().toString());
        });

        totalAmountField.setText(bill.getTotalAmount().toString());
        insuranceProviderField.setText(bill.getInsuranceProvider());
        policyNumberField.setText(bill.getInsurancePolicyNumber());
    }

    private void submitClaim() {
        String selectedBillLabel = (String) billComboBox.getSelectedItem();
        if (selectedBillLabel == null || selectedBillLabel.isEmpty()) {
            showError("Please select a bill first");
            return;
        }

        try {
            // Extract bill ID from the label (format: "Bill #12345678 - PatientName - $Amount")
            String billId = selectedBillLabel.substring(6, 14);

            // Find the full bill in the table data
            for (int i = 0; i < billsTableModel.getRowCount(); i++) {
                String fullBillId = (String) billsTableModel.getValueAt(i, 0);
                if (fullBillId.startsWith(billId)) {
                    billingController.getBill(UUID.fromString(fullBillId)).ifPresent(bill -> {
                        try {
                            billingController.submitClaim(bill);
                            bill.setStatus(BillStatus.INSURANCE_SUBMITTED); // Using correct enum value
                            billingController.updateBill(bill); // Save the updated bill
                            refreshBillsTable();  // Refresh to show updated status
                            JOptionPane.showMessageDialog(this,
                                "Insurance claim submitted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            showError("Failed to submit claim: " + ex.getMessage());
                        }
                    });
                    return;
                }
            }
            showError("Could not find the selected bill");
        } catch (Exception e) {
            showError("Error processing claim: " + e.getMessage());
        }
    }

    private void clearForm() {
        patientSelector.setSelectedIndex(0);
        insurancePolicyField.setText("");
        insuranceProviderField.setText("");
        itemsTableModel.setRowCount(0);
        notesArea.setText("");
    }

    private void refreshBillsTable() {
        billsTableModel.setRowCount(0);
        billComboBox.removeAllItems();  // Clear the bill combo box

        for (Bill bill : billingController.getAllBills()) {
            Optional<Patient> patientOpt = patientController.getPatient(bill.getPatientId().toString());
            String patientName = patientOpt.map(p -> p.getFirstName() + " " + p.getLastName())
                                         .orElse("Unknown");

            Object[] row = {
                bill.getId().toString(),
                patientName,
                bill.getTotalAmount(),
                bill.getStatus(),
                bill.getCoveredAmount(),
                bill.getRemainingAmount()
            };
            billsTableModel.addRow(row);

            // Add to bill combo box with a descriptive label
            String billLabel = String.format("Bill #%s - %s - $%s",
                bill.getId().toString().substring(0, 8),
                patientName,
                bill.getTotalAmount().toString());
            billComboBox.addItem(billLabel);
        }
    }

    private void refreshClaimsList() {
        // Clear and refresh claims list
        billsTableModel.setRowCount(0);
        billingController.getAllClaims().forEach(claim -> {
            billsTableModel.addRow(new Object[]{
                claim.getId().toString(),
                claim.getStatus().getDisplayName(),
                claim.getAmount(),
                claim.getApprovedAmount(),
                claim.getNotes()
            });
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

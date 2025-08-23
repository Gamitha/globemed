import com.globemed.core.security.SecurityService;
import com.globemed.core.security.SecurityServiceImpl;
import com.globemed.core.controller.PatientController;
import com.globemed.core.controller.AppointmentController;
import com.globemed.core.controller.DoctorController;
import com.globemed.core.controller.BillingController;
import com.globemed.core.view.PatientManagementPanel;
import com.globemed.core.view.AppointmentPanel;
import com.globemed.core.view.DoctorManagementPanel;
import com.globemed.core.view.BillingPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private static final String APP_TITLE = "GlobeMed Healthcare Management System";
    private static SecurityService securityService;
    private static PatientController patientController;
    private static AppointmentController appointmentController;
    private static DoctorController doctorController;
    private static BillingController billingController;
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel contentPanel;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new PlasticLookAndFeel());
            PlasticLookAndFeel.setCurrentTheme(new ExperienceBlue());

            SwingUtilities.invokeLater(() -> {
                try {
                    initializeApplication();
                } catch (Exception e) {
                    showError("Failed to initialize application", e);
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            showError("Failed to set look and feel", e);
            System.exit(1);
        }
    }

    private static void initializeApplication() {
        // Initialize core services
        securityService = new SecurityServiceImpl();
        patientController = new PatientController(securityService);
        doctorController = new DoctorController(securityService);
        appointmentController = new AppointmentController(securityService);
        billingController = new BillingController(securityService);

        // Show login dialog
        if (showLoginDialog()) {
            createAndShowMainWindow();
        } else {
            System.exit(0);
        }
    }

    private static boolean showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Login",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (securityService.authenticate(username, password)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Invalid username or password",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            } else {
                return false;
            }
        }
    }

    private static void createAndShowMainWindow() {
        mainFrame = new JFrame(APP_TITLE);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);

        FormLayout layout = new FormLayout(
                "4dlu, fill:200px:nogrow, 4dlu, fill:pref:grow, 4dlu",
                "4dlu, fill:pref:grow, 4dlu");

        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        // Create navigation sidebar
        builder.add(createSidebar(), cc.xy(2, 2));

        // Create main content area with card layout
        contentPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) contentPanel.getLayout();

        // Add views
        contentPanel.add(createWelcomePanel(), "WELCOME");
        contentPanel.add(new PatientManagementPanel(patientController), "PATIENTS");
        contentPanel.add(new DoctorManagementPanel(doctorController), "DOCTORS");
        contentPanel.add(new AppointmentPanel(appointmentController, patientController, doctorController), "APPOINTMENTS");
        contentPanel.add(new BillingPanel(billingController, patientController), "BILLING");

        builder.add(contentPanel, cc.xy(4, 2));
        cardLayout.show(contentPanel, "WELCOME");

        mainFrame.setContentPane(builder.getPanel());
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        addSidebarButton(sidebar, "Dashboard", "WELCOME");
        addSidebarButton(sidebar, "Patient Management", "PATIENTS");
        addSidebarButton(sidebar, "Doctor Management", "DOCTORS");
        addSidebarButton(sidebar, "Appointments", "APPOINTMENTS");
        addSidebarButton(sidebar, "Billing & Insurance", "BILLING");

        return sidebar;
    }

    private static void addSidebarButton(JPanel sidebar, String text, String cardName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private static JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to GlobeMed Healthcare Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel userLabel = new JLabel("Logged in as: " + securityService.getCurrentUser());
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(userLabel, BorderLayout.SOUTH);

        return panel;
    }

    private static void showError(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
                message + "\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
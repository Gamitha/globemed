import com.globemed.core.security.SecurityService;
import com.globemed.core.security.SecurityServiceImpl;
import com.globemed.core.controller.*;
import com.globemed.core.view.*;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String APP_TITLE = "GlobeMed Healthcare Management System";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static SecurityService securityService;
    private static PatientController patientController;
    private static AppointmentController appointmentController;
    private static DoctorController doctorController;
    private static BillingController billingController;
    private static ReportController reportController;
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
        reportController = new ReportController(securityService, patientController, billingController);

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
        mainFrame.setSize(1024, 768);
        mainFrame.setLocationRelativeTo(null);

        // Create the main split pane for sidebar and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); // Width of sidebar
        splitPane.setDividerSize(5);

        // Create and add sidebar
        JPanel sidebarPanel = createSidebar();
        splitPane.setLeftComponent(sidebarPanel);

        // Initialize card layout for main content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create and add all panels
        contentPanel.add(createWelcomePanel(), "WELCOME");
        contentPanel.add(new PatientManagementPanel(patientController), "PATIENTS");
        contentPanel.add(new AppointmentPanel(appointmentController, patientController, doctorController), "APPOINTMENTS");
        contentPanel.add(new DoctorManagementPanel(doctorController), "DOCTORS");
        contentPanel.add(new BillingPanel(billingController, patientController), "BILLING");
        contentPanel.add(new ReportPanel(reportController, patientController), "REPORTS");

        splitPane.setRightComponent(contentPanel);

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        mainFrame.setJMenuBar(menuBar);

        mainFrame.add(splitPane);
        mainFrame.setVisible(true);

        // Show welcome panel by default
        cardLayout.show(contentPanel, "WELCOME");
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);

        // Add view menu items
        addMenuItem(viewMenu, "Welcome", "WELCOME");
        addMenuItem(viewMenu, "Patients", "PATIENTS");
        addMenuItem(viewMenu, "Appointments", "APPOINTMENTS");
        addMenuItem(viewMenu, "Doctors", "DOCTORS");
        addMenuItem(viewMenu, "Billing", "BILLING");
        addMenuItem(viewMenu, "Reports", "REPORTS");

        // Add file menu items
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            mainFrame.dispose();
            if (showLoginDialog()) {
                createAndShowMainWindow();
            } else {
                System.exit(0);
            }
        });
        fileMenu.add(logoutItem);

        addMenuItem(fileMenu, "Exit", () -> System.exit(0));

        return menuBar;
    }

    private static void addMenuItem(JMenu menu, String label, String cardName) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        menu.add(menuItem);
    }

    private static void addMenuItem(JMenu menu, String label, Runnable action) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(e -> action.run());
        menu.add(menuItem);
    }

    private static JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sidebar.setBackground(new Color(240, 240, 240)); // Light gray background

        addSidebarButton(sidebar, "Dashboard", "WELCOME");
        addSidebarButton(sidebar, "Patient Management", "PATIENTS");
        addSidebarButton(sidebar, "Doctor Management", "DOCTORS");
        addSidebarButton(sidebar, "Appointments", "APPOINTMENTS");
        addSidebarButton(sidebar, "Billing & Insurance", "BILLING");
        addSidebarButton(sidebar, "Reports", "REPORTS");

        // Add glue to push buttons to top
        sidebar.add(Box.createVerticalGlue());

        // Add logged in user info at bottom
        JLabel userLabel = new JLabel("User: " + securityService.getCurrentUser());
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sidebar.add(userLabel);

        return sidebar;
    }

    private static void addSidebarButton(JPanel sidebar, String text, String cardName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        // Style the button
        button.setFocusPainted(false);
        button.setBackground(new Color(245, 245, 245));
        button.setBorderPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
            }
        });

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private static JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to GlobeMed Healthcare Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add dashboard content
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Add dashboard cards
        addDashboardCard(dashboardPanel, "Patients", "Manage patient records and medical history");
        addDashboardCard(dashboardPanel, "Doctors", "Manage doctor schedules and specializations");
        addDashboardCard(dashboardPanel, "Appointments", "Schedule and manage appointments");
        addDashboardCard(dashboardPanel, "Billing", "Handle billing and insurance claims");
        addDashboardCard(dashboardPanel, "Reports", "Generate and view various reports");
        addDashboardCard(dashboardPanel, "Settings", "Configure system settings and preferences");

        // User info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel userLabel = new JLabel("Logged in as: " + securityService.getCurrentUser());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        footerPanel.add(userLabel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(dashboardPanel, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static void addDashboardCard(JPanel dashboard, String title, String description) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel descLabel = new JLabel("<html><body style='width: 150px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);

        dashboard.add(card);
    }

    private static void showError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
        JOptionPane.showMessageDialog(null,
                message + "\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
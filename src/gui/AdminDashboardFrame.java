package gui;

import service.UserService;
import util.SessionManager;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class AdminDashboardFrame extends JFrame {
    private final UserService userService;

    public AdminDashboardFrame(UserService userService) {
        this.userService = userService;
        setTitle("EVO Admin Dashboard");
        setSize(480, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        String name = SessionManager.getCurrentUser().getFullName();
        JLabel titleLabel = new JLabel("Selamat datang, " + name + " (Admin)", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(18f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton manageUsersButton = new JButton("User Management");
        manageUsersButton.addActionListener(event -> new UserManagementFrame(userService).setVisible(true));

        JButton manageClientsButton = new JButton("Client Management");
        manageClientsButton.addActionListener(event -> new ClientManagementFrame().setVisible(true));

        JButton manageEventsButton = new JButton("Event Management");
        manageEventsButton.addActionListener(event -> new EventManagementFrame().setVisible(true));

        JButton manageVendorsButton = new JButton("Vendor Management"); // <-- BARU
        manageVendorsButton.addActionListener(event -> new VendorManagementFrame().setVisible(true));

        JButton managePaymentsButton = new JButton("Payment Management");
        managePaymentsButton.addActionListener(event -> new PaymentManagementFrame().setVisible(true));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logout());

        menuPanel.add(manageUsersButton);
        menuPanel.add(manageClientsButton);
        menuPanel.add(manageEventsButton);
        menuPanel.add(manageVendorsButton); // <-- taruh sebelum Logout
        menuPanel.add(managePaymentsButton); 
        menuPanel.add(logoutButton);
        add(menuPanel, BorderLayout.CENTER);
    }

    private void logout() {
        SessionManager.logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}

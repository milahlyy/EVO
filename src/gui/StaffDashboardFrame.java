package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import util.SessionManager;

public class StaffDashboardFrame extends JFrame {

    public StaffDashboardFrame() {
        setTitle("EVO Staff Dashboard");
        setSize(480, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        String name = SessionManager.getCurrentUser().getFullName();
        JLabel titleLabel = new JLabel("Selamat datang, " + name + " (Staff)", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(18f));
        add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton manageClientsButton = new JButton("Client Management");
        manageClientsButton.addActionListener(event -> new ClientManagementFrame().setVisible(true));
        panel.add(manageClientsButton);

        JButton manageVendorsButton = new JButton("Vendor Management"); // <-- BARU
        manageVendorsButton.addActionListener(event -> new VendorManagementFrame().setVisible(true));
        panel.add(manageVendorsButton);

        JButton manageEventsButton = new JButton("Event Management"); // <-- BARU
        manageEventsButton.addActionListener(event -> new EventManagementFrame().setVisible(true));
        panel.add(manageEventsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logout());
        panel.add(logoutButton);

        add(panel, BorderLayout.CENTER);
    }

    private void logout() {
        SessionManager.logout();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
